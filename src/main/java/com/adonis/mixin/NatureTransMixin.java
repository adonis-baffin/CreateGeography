package com.adonis.mixin;

import com.adonis.utils.BlockTransformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MudBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import static com.adonis.fluid.GeographyFluids.BRINE;
import static com.adonis.registry.BlockRegistry.SALINE_MUD;

@Mixin(MudBlock.class)
public abstract class NatureTransMixin extends Block {

    public NatureTransMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 检查附近是否有盐水
        if (BlockTransformUtils.isFluidNearby(level, pos, BRINE.get().getSource(), 4)) {

            if (random.nextFloat() < 0.2f) {
                if (SALINE_MUD.get() != null) {
                    level.setBlock(pos, SALINE_MUD.get().defaultBlockState(), 3);
                }
            }
            return; // 如果检测到盐水，就不再检查红树林逻辑
        }

        // 原有的红树林沼泽转换逻辑
        if (isInMangroveSwamp(level, pos)) {
            if (!hasSalineMudWithinManhattanDistance(level, pos, 8)) {
                if (SALINE_MUD.get() != null) {
                    level.setBlock(pos, SALINE_MUD.get().defaultBlockState(), 3);
                }
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true; // 确保随机刻生效
    }

    private boolean isInMangroveSwamp(LevelReader level, BlockPos pos) {
        return BlockTransformUtils.isSpecificBiome(level, pos, "mangrove_swamp");
    }

    private boolean hasSalineMudWithinManhattanDistance(ServerLevel level, BlockPos pos, int distance) {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        for (int dx = -distance; dx <= distance; dx++) {
            for (int dy = -distance; dy <= distance; dy++) {
                for (int dz = -distance; dz <= distance; dz++) {
                    int manhattanDist = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                    if (manhattanDist <= distance && manhattanDist > 0) {
                        mutablePos.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                        if (level.getBlockState(mutablePos).is(SALINE_MUD.get())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}