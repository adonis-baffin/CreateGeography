package com.adonis.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static com.adonis.fluid.GeographyFluids.BRINE;
import static com.adonis.registry.BlockRegistry.SALINE_FARMLAND;


@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        // 检查附近是否有盐水
        if (isNearFluid(level, pos, BRINE.get().getSource())) {
            level.setBlock(pos, SALINE_FARMLAND.get().defaultBlockState(), 3);
            ci.cancel(); // 取消原版逻辑
        }
    }

    private boolean isNearFluid(ServerLevel level, BlockPos pos, Fluid fluid) {
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            FluidState fluidState = level.getFluidState(checkPos);
            if (fluidState.getType() == fluid) {
                return true;
            }
        }
        return false;
    }
}