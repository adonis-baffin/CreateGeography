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
        return true; // 启用随机刻，用于处理脱盐和盐晶生成
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isClientSide) {
            return;
        }

        // 脱盐逻辑 - 优先检查脱盐
        NaturalTransformHandler.handleSalineSoilDesalination(pState, pLevel, pPos, pRandom);

        // 如果方块已经被脱盐替换，直接返回
        if (pLevel.getBlockState(pPos).getBlock() != this) {
            return;
        }

        // 盐晶生成逻辑 - 使用统一的处理器
        if (NaturalTransformConfig.ENABLE_SALT_CRYSTAL_GENERATION.get()) {
            NaturalTransformHandler.handleSaltCrystalGeneration(pState, pLevel, pPos, pRandom);
        }

        // 处理盐碱土壤的随机刻行为（盐晶生成）
        NaturalTransformHandler.handleSalineSoilRandomTick(pState, pLevel, pPos, pRandom);
    }
}