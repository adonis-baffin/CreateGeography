package com.adonis.content.block; // 请确保包名和你的项目结构一致

import com.adonis.fluid.GeographyFluids;
import com.adonis.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SalineMudBlock extends Block {

    public SalineMudBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isClientSide) {
            return;
        }

        // 检查上方方块是否为空气，为盐晶生成提供空间
        BlockPos abovePos = pPos.above();
        if (pLevel.isEmptyBlock(abovePos)) {

            // 新逻辑：检查同Y轴平面上，曼哈顿距离5以内是否有卤水
            boolean hasBrineNearby = false;
            searchLoop: // 使用标签以便跳出外层循环
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    // 计算平面曼哈顿距离
                    if (Math.abs(dx) + Math.abs(dz) <= 5) {
                        BlockPos checkPos = pPos.offset(dx, 0, dz); // dy为0，确保在同一Y层
                        if (pLevel.getFluidState(checkPos).is(GeographyFluids.BRINE.get().getSource())) {
                            hasBrineNearby = true;
                            break searchLoop; // 找到一个就足够了，跳出所有循环
                        }
                    }
                }
            }

            // 如果附近有卤水，则有 15% 的概率在其上方生成一层盐晶
            if (hasBrineNearby && pRandom.nextFloat() < 0.15f) {
                BlockState saltCrystalState = BlockRegistry.SALT_CRYSTAL.get().defaultBlockState();
                pLevel.setBlock(abovePos, saltCrystalState, 3);
            }
        }
    }
}