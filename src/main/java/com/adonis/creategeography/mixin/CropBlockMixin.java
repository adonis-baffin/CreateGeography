package com.adonis.creategeography.mixin;

import com.adonis.creategeography.content.block.SalineFarmlandBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        BlockState farmlandState = level.getBlockState(pos.below());

        // 检查是否在盐碱耕地上
        if (farmlandState.getBlock() instanceof SalineFarmlandBlock) {
            int salinity = farmlandState.getValue(SalineFarmlandBlock.SALINITY);

            // 根据盐碱化等级控制生长
            boolean shouldCancelGrowth = false;

            switch (salinity) {
                case 0: // 轻度盐碱化 - 50%概率阻止生长
                    shouldCancelGrowth = random.nextFloat() < 0.5f;
                    break;

                case 1: // 中度盐碱化 - 75%概率阻止生长
                    shouldCancelGrowth = random.nextFloat() < 0.75f;
                    break;

                case 2: // 重度盐碱化 - 完全停止生长
                    shouldCancelGrowth = true;
                    break;

                case 3: // 极度盐碱化 - 完全停止生长（退化在SalineFarmlandBlock中处理）
                    shouldCancelGrowth = true;
                    break;
            }

            if (shouldCancelGrowth) {
                ci.cancel(); // 取消原版的生长逻辑
            }
            // 如果不取消，让作物正常生长（按减缓的概率）
        }
    }
}