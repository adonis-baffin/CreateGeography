package com.adonis;

import com.adonis.fluid.GeographyFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = CreateGeography.MODID)
public class ColdRegionNatureTransHandler {

    private static final int TICK_INTERVAL = 80; // 每80tick（4秒）检查一次
    private static final double ICE_CRACK_PROBABILITY = 0.8; // 冰方块触发概率
    private static final double SOIL_FREEZE_PROBABILITY = 1; // 泥土类方块触发概率
    private static final int MAX_CONSECUTIVE_CRACKED = 5; // 连续破裂冰的最大数量
    private static final int BLOCKING_DISTANCE = 14; // 阻断转换的曼哈顿距离

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        // 每 TICK_INTERVAL 刻执行一次
        if (event.getServer().getTickCount() % TICK_INTERVAL != 0) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            level.getProfiler().push("coldRegionNatureTrans");
            for (var player : level.players()) {
                BlockPos playerPos = player.blockPosition();
                RandomSource random = level.random;

                // 随机检查附近10个方块
                for (int i = 0; i < 8; i++) {
                    BlockPos pos = playerPos.offset(
                            random.nextInt(16) - 8,
                            random.nextInt(8) - 4,
                            random.nextInt(16) - 8
                    );

                    // 检查是否为寒冷群系
                    Biome biome = level.getBiome(pos).value();
                    if (!biome.coldEnoughToSnow(pos)) continue;

                    BlockState state = level.getBlockState(pos);
                    Block currentBlock = state.getBlock();

                    // 处理冰方块（ICE, PACKED_ICE, BLUE_ICE）
                    if (currentBlock == Blocks.ICE || currentBlock == Blocks.PACKED_ICE || currentBlock == Blocks.BLUE_ICE) {
                        // 模拟随机刻概率
                        if (random.nextDouble() > ICE_CRACK_PROBABILITY) continue;

                        // 触发方块更新
                        level.scheduleTick(pos, currentBlock, 1);

                        // 重新获取方块状态
                        state = level.getBlockState(pos);
                        currentBlock = state.getBlock();

                        // 检查天空暴露
                        if (!level.canSeeSky(pos.above())) continue;

                        Block targetBlock = null;
                        Block crackedBlock = null;

                        // 确定目标破裂方块
                        if (currentBlock == Blocks.ICE) {
                            targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CreateGeography.MODID, "cracked_ice"));
                            crackedBlock = targetBlock;
                        } else if (currentBlock == Blocks.PACKED_ICE) {
                            targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CreateGeography.MODID, "cracked_packed_ice"));
                            crackedBlock = targetBlock;
                        } else if (currentBlock == Blocks.BLUE_ICE) {
                            targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CreateGeography.MODID, "cracked_blue_ice"));
                            crackedBlock = targetBlock;
                        }

                        if (targetBlock != null && targetBlock != Blocks.AIR) {
                            // 检查是否位于连续破裂冰阻断范围内
                            if (isNearConsecutiveCrackedIce(level, pos, crackedBlock)) {
                                continue; // 如果在连续破裂冰的阻断范围内，阻止转换
                            }

                            // 检查X轴上4格内是否有同类破裂冰
                            boolean hasCrackedNearby = false;
                            for (int x = -4; x <= 4; x++) {
                                if (x != 0) {
                                    BlockPos checkPos = pos.offset(x, 0, 0);
                                    if (level.getBlockState(checkPos).getBlock() == crackedBlock) {
                                        hasCrackedNearby = true;
                                        break;
                                    }
                                }
                            }

                            // 如果X轴上4格内没有破裂冰，99%概率驳回
                            if (!hasCrackedNearby && random.nextFloat() < 0.99f) continue;

                            // 替换为破裂方块
                            level.setBlock(pos, targetBlock.defaultBlockState(), 3);
                        }
                    }
                    // 处理泥土类方块（DIRT, MUD, GRASS_BLOCK, DIRT_PATH, ROOTED_DIRT, PODZOL）
                    else if (isSoilBlock(currentBlock)) {
                        // 模拟随机刻概率
                        if (random.nextDouble() > SOIL_FREEZE_PROBABILITY) continue;

                        // 触发方块更新
                        level.scheduleTick(pos, currentBlock, 1);

                        // 重新获取方块状态
                        state = level.getBlockState(pos);
                        currentBlock = state.getBlock();
                        if (!isSoilBlock(currentBlock)) continue;

                        Block frozenSoil = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(CreateGeography.MODID, "frozen_soil"));

                        if (frozenSoil == null || frozenSoil == Blocks.AIR) continue;

                        // 检查直接邻接的水或冰
                        boolean adjacentToWaterOrIce = false;
                        for (BlockPos neighbor : new BlockPos[] {
                                pos.north(), pos.south(), pos.east(), pos.west(), pos.above(), pos.below()
                        }) {
                            BlockState neighborState = level.getBlockState(neighbor);
                            Block neighborBlock = neighborState.getBlock();
                            if (neighborBlock == Blocks.ICE || neighborBlock == Blocks.PACKED_ICE || neighborBlock == Blocks.BLUE_ICE) {
                                adjacentToWaterOrIce = true;
                                break;
                            } else if (neighborState.getFluidState().getType() == Fluids.WATER) {
                                adjacentToWaterOrIce = true;
                                break;
                            }
                        }

                        // 情况1：邻接水或冰，直接转化
                        if (adjacentToWaterOrIce) {
                            level.setBlock(pos, frozenSoil.defaultBlockState(), 3);
                        }
                        // 情况2：邻接冻土，检查曼哈顿距离3内是否有水
                        else {
                            boolean adjacentToFrozenSoil = false;
                            for (BlockPos neighbor : new BlockPos[] {
                                    pos.north(), pos.south(), pos.east(), pos.west(), pos.above(), pos.below()
                            }) {
                                if (level.getBlockState(neighbor).getBlock() == frozenSoil) {
                                    adjacentToFrozenSoil = true;
                                    break;
                                }
                            }

                            if (adjacentToFrozenSoil) {
                                boolean hasWaterNearby = false;
                                // 三维曼哈顿距离检查
                                for (int x = -2; x <= 2; x++) {
                                    for (int y = -2; y <= 2; y++) {
                                        for (int z = -2; z <= 2; z++) {
                                            if (Math.abs(x) + Math.abs(y) + Math.abs(z) < 3 && !(x == 0 && y == 0 && z == 0)) {
                                                BlockPos checkPos = pos.offset(x, y, z);
                                                BlockState checkState = level.getBlockState(checkPos);
                                                if (checkState.getFluidState().getType() == Fluids.WATER) {
                                                    hasWaterNearby = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (hasWaterNearby) break; // 提前退出 Y 循环
                                    }
                                    if (hasWaterNearby) break; // 提前退出 X 循环
                                }

                                if (hasWaterNearby) {
                                    level.setBlock(pos, frozenSoil.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }
            level.getProfiler().pop();
        }
    }

    // 判断是否为泥土类方块
    private static boolean isSoilBlock(Block block) {
        return block == Blocks.DIRT ||
                block == Blocks.GRASS_BLOCK ||
                block == Blocks.DIRT_PATH ||
                block == Blocks.ROOTED_DIRT ||
                block == Blocks.PODZOL;
    }

    /**
     * 检查该位置是否在连续破裂冰的阻断范围内
     */
    private static boolean isNearConsecutiveCrackedIce(ServerLevel level, BlockPos pos, Block crackedBlock) {
        if (crackedBlock == null) return false;

        // 搜索半径
        int searchRadius = 32; // 搜索范围扩大，确保能找到所有可能的连续破裂冰

        // 存储找到的连续破裂冰的位置
        List<List<BlockPos>> consecutiveCrackedGroups = new ArrayList<>();

        // 搜索X方向的连续破裂冰
        for (int x = -searchRadius; x <= searchRadius - MAX_CONSECUTIVE_CRACKED + 1; x++) {
            for (int z = -searchRadius; z <= searchRadius; z++) {
                int consecutiveCount = 0;
                List<BlockPos> currentGroup = new ArrayList<>();

                for (int i = 0; i < MAX_CONSECUTIVE_CRACKED + 1; i++) { // +1 确保我们能找到至少5个连续的
                    BlockPos checkPos = pos.offset(x + i, 0, z);
                    if (level.getBlockState(checkPos).getBlock() == crackedBlock) {
                        consecutiveCount++;
                        currentGroup.add(checkPos);
                    } else {
                        break; // 连续性中断
                    }
                }

                if (consecutiveCount >= MAX_CONSECUTIVE_CRACKED + 1) { // 找到至少5个连续的
                    consecutiveCrackedGroups.add(currentGroup);
                }
            }
        }

        // 搜索Z方向的连续破裂冰
        for (int z = -searchRadius; z <= searchRadius - MAX_CONSECUTIVE_CRACKED + 1; z++) {
            for (int x = -searchRadius; x <= searchRadius; x++) {
                int consecutiveCount = 0;
                List<BlockPos> currentGroup = new ArrayList<>();

                for (int i = 0; i < MAX_CONSECUTIVE_CRACKED + 1; i++) { // +1 确保我们能找到至少5个连续的
                    BlockPos checkPos = pos.offset(x, 0, z + i);
                    if (level.getBlockState(checkPos).getBlock() == crackedBlock) {
                        consecutiveCount++;
                        currentGroup.add(checkPos);
                    } else {
                        break; // 连续性中断
                    }
                }

                if (consecutiveCount >= MAX_CONSECUTIVE_CRACKED + 1) { // 找到至少5个连续的
                    consecutiveCrackedGroups.add(currentGroup);
                }
            }
        }

        // 检查当前位置是否在任何一组连续破裂冰的阻断范围内
        for (List<BlockPos> group : consecutiveCrackedGroups) {
            for (BlockPos crackedPos : group) {
                // 计算曼哈顿距离
                int manhattanDistance = Math.abs(pos.getX() - crackedPos.getX()) +
                        Math.abs(pos.getY() - crackedPos.getY()) +
                        Math.abs(pos.getZ() - crackedPos.getZ());

                if (manhattanDistance <= BLOCKING_DISTANCE) {
                    return true; // 在阻断范围内
                }
            }
        }

        return false; // 不在任何连续破裂冰的阻断范围内
    }
}