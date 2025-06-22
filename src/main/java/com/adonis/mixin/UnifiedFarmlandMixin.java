package com.adonis.mixin;

import com.adonis.transform.FarmlandTransformHandler;
import com.adonis.CreateGeography;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 统一的耕地Mixin - 处理所有耕地类型的转换
 * 这个Mixin会拦截所有继承自FarmBlock的方块的randomTick
 */
@Mixin(FarmBlock.class)
public class UnifiedFarmlandMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        try {
            // 调用我们的处理器
            boolean shouldCancelVanilla = FarmlandTransformHandler.processRandomTick(state, level, pos, random);

            if (shouldCancelVanilla) {
                // 取消原版randomTick逻辑
                ci.cancel();

                // 可选：输出调试信息
                if (random.nextInt(200) == 0) { // 0.5%概率，避免日志过多
                    CreateGeography.LOGGER.debug("🌾 Farmland custom logic at {}", pos);
                }
            }

        } catch (Exception e) {
            CreateGeography.LOGGER.error("❌ Error in unified farmland mixin at {}: ", pos, e);
            // 出错时不取消原版逻辑，确保游戏稳定性
        }
    }
}