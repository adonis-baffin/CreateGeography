package com.adonis.content.block;

import com.adonis.config.NaturalTransformConfig;
import com.adonis.event.NaturalTransformHandler;
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
        return NaturalTransformConfig.ENABLE_SALT_CRYSTAL_GENERATION.get();
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!NaturalTransformConfig.ENABLE_SALT_CRYSTAL_GENERATION.get() || pLevel.isClientSide) {
            return;
        }

        // 使用统一的盐晶生成处理器
        NaturalTransformHandler.handleSaltCrystalGeneration(pState, pLevel, pPos, pRandom);
    }
}