package com.adonis.mixin;

import com.adonis.transform.FarmlandTransformHandler;
import com.adonis.CreateGeography;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 自定义耕地方块的Mixin - 处理BlackFarmlandBlock和SalineFarmlandBlock
 */
@Mixin({
        com.adonis.content.block.BlackFarmlandBlock.class,
        com.adonis.content.block.SalineFarmlandBlock.class
})
public class CustomFarmlandMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        try {
            // 调用统一的处理器
            boolean shouldCancelVanilla = FarmlandTransformHandler.processRandomTick(state, level, pos, random);

            if (shouldCancelVanilla) {
                // 取消原版randomTick逻辑
                ci.cancel();

                // 调试信息
                if (random.nextInt(200) == 0) {
                    CreateGeography.LOGGER.debug("🌾 Custom farmland logic at {} for {}", pos, state.getBlock());
                }
            }

        } catch (Exception e) {
            CreateGeography.LOGGER.error("❌ Error in custom farmland mixin at {}: ", pos, e);
            // 出错时不取消原版逻辑
        }
    }
}