package com.adonis.mixin;

import com.adonis.utils.BlockTransformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MudBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

import static com.adonis.fluid.GeographyFluids.BRINE;
import static com.adonis.registry.BlockRegistry.SALINE_MUD;
import static com.adonis.registry.BlockRegistry.FROZEN_SOIL;

@Mixin(MudBlock.class)
public abstract class MudBlockMixin extends Block {

    private static final int MAX_CONSECUTIVE_SALINE = 4; // 连续盐碱泥的最大数量
    private static final int BLOCKING_DISTANCE = 14; // 阻断转换的曼哈顿距离

    public MudBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 1. 检查寒冷群系转为冻土的逻辑
        if (isColdBiome(level, pos)) {
            if (random.nextFloat() < 0.2f) {
                if (FROZEN_SOIL.get() != null) {
                    level.setBlock(pos, FROZEN_SOIL.get().defaultBlockState(), 3);
                    return;
                }
            }
        }

        // 2. 盐水使泥巴盐碱化的逻辑 - 使用与泥土变成冻土一样的逻辑
        processBrineTransformation(state, level, pos);

        // 3. 红树林中泥巴盐碱化逻辑 - 与破裂冰相同的逻辑
        if (isInMangroveSwamp(level, pos)) {
            // 检查是否在连续盐碱泥的阻断范围内
            if (isNearConsecutiveSalineMud(level, pos)) {
                return; // 如果在连续盐碱泥的阻断范围内，阻止转换
            }

            // 检查X轴上4格内是否有盐碱泥
            boolean hasSalineMudNearby = false;
            for (int x = -4; x <= 4; x++) {
                if (x != 0) {
                    BlockPos checkPos = pos.offset(x, 0, 0);
                    if (level.getBlockState(checkPos).is(SALINE_MUD.get())) {
                        hasSalineMudNearby = true;
                        break;
                    }
                }
            }

            // 如果X轴上4格内没有盐碱泥，99%概率驳回
            if (!hasSalineMudNearby && random.nextFloat() < 0.99f) {
                return;
            }

            // 替换为盐碱泥
            if (SALINE_MUD.get() != null) {
                level.setBlock(pos, SALINE_MUD.get().defaultBlockState(), 3);
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true; // 确保随机刻生效
    }

    private boolean isColdBiome(LevelReader level, BlockPos pos) {
        return level.getBiome(pos).value().coldEnoughToSnow(pos);
    }

    private boolean isInMangroveSwamp(LevelReader level, BlockPos pos) {
        return BlockTransformUtils.isSpecificBiome(level, pos, "mangrove_swamp");
    }

    /**
     * 使用与泥土变成冻土相同的逻辑处理盐水导致的泥巴盐碱化
     */
    private void processBrineTransformation(BlockState state, ServerLevel level, BlockPos pos) {
        if (SALINE_MUD.get() == null) return;

        Fluid brineFluid = BRINE.get().getSource();

        // 检查直接邻接的盐水
        boolean adjacentToBrine = false;
        for (BlockPos neighbor : new BlockPos[] {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.above(), pos.below()
        }) {
            if (level.getFluidState(neighbor).getType() == brineFluid) {
                adjacentToBrine = true;
                break;
            }
        }

        // 情况1：邻接盐水，直接转化
        if (adjacentToBrine) {
            level.setBlock(pos, SALINE_MUD.get().defaultBlockState(), 3);
            return;
        }

        // 情况2：邻接盐碱泥，检查曼哈顿距离3内是否有盐水
        boolean adjacentToSalineMud = false;
        for (BlockPos neighbor : new BlockPos[] {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.above(), pos.below()
        }) {
            if (level.getBlockState(neighbor).is(SALINE_MUD.get())) {
                adjacentToSalineMud = true;
                break;
            }
        }

        if (adjacentToSalineMud) {
            boolean hasBrineNearby = false;
            // 三维曼哈顿距离检查
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) < 3 && !(x == 0 && y == 0 && z == 0)) {
                            BlockPos checkPos = pos.offset(x, y, z);
                            if (level.getFluidState(checkPos).getType() == brineFluid) {
                                hasBrineNearby = true;
                                break;
                            }
                        }
                    }
                    if (hasBrineNearby) break; // 提前退出 Y 循环
                }
                if (hasBrineNearby) break; // 提前退出 X 循环
            }

            if (hasBrineNearby) {
                level.setBlock(pos, SALINE_MUD.get().defaultBlockState(), 3);
            }
        }
    }

    /**
     * 检查该位置是否在连续盐碱泥的阻断范围内
     */
    private boolean isNearConsecutiveSalineMud(ServerLevel level, BlockPos pos) {
        if (SALINE_MUD.get() == null) return false;

        // 搜索半径
        int searchRadius = 32; // 搜索范围扩大，确保能找到所有可能的连续盐碱泥

        // 存储找到的连续盐碱泥的位置
        List<List<BlockPos>> consecutiveSalineGroups = new ArrayList<>();

        // 搜索X方向的连续盐碱泥
        for (int x = -searchRadius; x <= searchRadius - MAX_CONSECUTIVE_SALINE + 1; x++) {
            for (int z = -searchRadius; z <= searchRadius; z++) {
                int consecutiveCount = 0;
                List<BlockPos> currentGroup = new ArrayList<>();

                for (int i = 0; i < MAX_CONSECUTIVE_SALINE + 1; i++) { // +1 确保我们能找到至少5个连续的
                    BlockPos checkPos = pos.offset(x + i, 0, z);
                    if (level.getBlockState(checkPos).is(SALINE_MUD.get())) {
                        consecutiveCount++;
                        currentGroup.add(checkPos);
                    } else {
                        break; // 连续性中断
                    }
                }

                if (consecutiveCount >= MAX_CONSECUTIVE_SALINE) { // 找到至少5个连续的
                    consecutiveSalineGroups.add(currentGroup);
                }
            }
        }

        // 搜索Z方向的连续盐碱泥
        for (int z = -searchRadius; z <= searchRadius - MAX_CONSECUTIVE_SALINE + 1; z++) {
            for (int x = -searchRadius; x <= searchRadius; x++) {
                int consecutiveCount = 0;
                List<BlockPos> currentGroup = new ArrayList<>();

                for (int i = 0; i < MAX_CONSECUTIVE_SALINE + 1; i++) { // +1 确保我们能找到至少5个连续的
                    BlockPos checkPos = pos.offset(x, 0, z + i);
                    if (level.getBlockState(checkPos).is(SALINE_MUD.get())) {
                        consecutiveCount++;
                        currentGroup.add(checkPos);
                    } else {
                        break; // 连续性中断
                    }
                }

                if (consecutiveCount >= MAX_CONSECUTIVE_SALINE) { // 找到至少5个连续的
                    consecutiveSalineGroups.add(currentGroup);
                }
            }
        }

        // 检查当前位置是否在任何一组连续盐碱泥的阻断范围内
        for (List<BlockPos> group : consecutiveSalineGroups) {
            for (BlockPos salinePos : group) {
                // 计算曼哈顿距离
                int manhattanDistance = Math.abs(pos.getX() - salinePos.getX()) +
                        Math.abs(pos.getY() - salinePos.getY()) +
                        Math.abs(pos.getZ() - salinePos.getZ());

                if (manhattanDistance <= BLOCKING_DISTANCE) {
                    return true; // 在阻断范围内
                }
            }
        }

        return false; // 不在任何连续盐碱泥的阻断范围内
    }
}