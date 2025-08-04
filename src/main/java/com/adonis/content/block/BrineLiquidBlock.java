package com.adonis.content.block;

import com.adonis.config.NaturalTransformConfig;
import com.adonis.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class BrineLiquidBlock extends LiquidBlock {

    public BrineLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    /**
     * 重写此方法进行优化。
     * 只有源方块才会触发随机刻，流动的盐水不会，以提高性能。
     */
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getFluidState().isSource();
    }

    /**
     * 方块的随机刻。
     * 这是处理盐水自然转换的核心逻辑。它由Minecraft引擎自动、高效地调用。
     * 这段代码直接从 NaturalTransformHandler.handleBrineRandomTick 迁移而来。
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!NaturalTransformConfig.ENABLE_BRINE_TRANSFORM.get()) {
            return;
        }

        // 我们已经在 isRandomlyTicking 中确保了这是源方块，所以这里的检查可以简化。
        // 不再需要检查 state.getFluidState()，因为这个方法只对本方块有效。

        int range = NaturalTransformConfig.BRINE_TRANSFORM_RANGE.get();

        // 遍历周围方块
        for (BlockPos nearbyPos : BlockPos.betweenClosed(
                pos.offset(-range, -1, -range),
                pos.offset(range, 1, range))) {

            if (nearbyPos.equals(pos)) continue;

            BlockState nearbyState = level.getBlockState(nearbyPos);

            // 处理耕地湿润和盐碱化
            if (nearbyState.getBlock() instanceof FarmBlock || nearbyState.getBlock() == BlockRegistry.SALINE_FARMLAND.get()) {
                // 调用私有辅助方法处理耕地逻辑
                this.handleFarmlandHydration(level, nearbyPos, nearbyState, random);
            }
            // 处理其他土壤转换
            else if (random.nextFloat() < NaturalTransformConfig.BRINE_TRANSFORM_CHANCE.get().floatValue()) {
                // 调用私有辅助方法处理土壤转换
                this.transformSoilToSaline(level, nearbyPos, nearbyState);
            }
        }
    }

    /**
     * 处理耕地的湿润和盐碱化。
     * 从 NaturalTransformHandler 迁移而来，并设为私有方法。
     */
    private void handleFarmlandHydration(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getBlock() instanceof FarmBlock) {
            // 普通耕地被盐水湿润时转换为盐碱耕地
            // 你的 FarmBlockMixin 确保了耕地可以被盐水湿润，这里处理转换的后果。
            if (random.nextFloat() < NaturalTransformConfig.BRINE_TRANSFORM_CHANCE.get().floatValue()) {
                level.setBlock(pos,
                        BlockRegistry.SALINE_FARMLAND.get().defaultBlockState()
                                .setValue(SalineFarmlandBlock.MOISTURE, 7) // 盐水直接湿润到满
                                .setValue(SalineFarmlandBlock.SALINITY, 1), 2);
            }
        } else if (state.getBlock() == BlockRegistry.SALINE_FARMLAND.get()) {
            int currentSalinity = state.getValue(SalineFarmlandBlock.SALINITY);

            // 盐水使盐碱耕地盐度增加
            if (currentSalinity < SalineFarmlandBlock.MAX_SALINITY &&
                    random.nextFloat() < NaturalTransformConfig.BRINE_TRANSFORM_CHANCE.get().floatValue()) {
                level.setBlock(pos, state
                        .setValue(SalineFarmlandBlock.MOISTURE, 7)
                        .setValue(SalineFarmlandBlock.SALINITY, currentSalinity + 1), 2);
            } else if (state.getValue(SalineFarmlandBlock.MOISTURE) < 7) {
                // 如果盐度已满或未成功增加，至少要确保它是湿润的
                level.setBlock(pos, state.setValue(SalineFarmlandBlock.MOISTURE, 7), 2);
            }
        }
    }

    /**
     * 转换土壤为盐碱土壤。
     * 从 NaturalTransformHandler 迁移而来，并设为私有方法。
     */
    private void transformSoilToSaline(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.is(Blocks.DIRT)) {
            level.setBlock(pos, BlockRegistry.SALINE_DIRT.get().defaultBlockState(), 2);
        } else if (state.is(Blocks.MUD)) {
            level.setBlock(pos, BlockRegistry.SALINE_MUD.get().defaultBlockState(), 2);
        }
        // 对于耕地的转换逻辑已经在 handleFarmlandHydration 中处理，这里可以省略，
        // 避免重复判断和潜在的逻辑冲突。
    }
}