package com.adonis.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.adonis.fluid.GeographyFluids.BRINE;
import static com.adonis.registry.BlockRegistry.SALINE_SOIL;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        // 检查附近是否有盐水
        if (isNearSaltWater(level, pos)) {
            // 将农田转换为盐碱耕地
            level.setBlock(pos, SALINE_SOIL.get().defaultBlockState(), 2);
            ci.cancel(); // 取消原版逻辑
        }
    }

    private boolean isNearSaltWater(LevelReader level, BlockPos pos) {
        // 假设BRINE.get().getSource()可以获取源流体
        Fluid brineFluid = BRINE.get().getSource();

        // 检查以农田为中心的 4 格范围内的流体
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            FluidState fluidState = level.getFluidState(checkPos);
            if (fluidState.getType() == brineFluid) {
                return true;
            }
        }
        return false;
    }
}