package com.adonis.mixin;

import com.adonis.fluid.GeographyFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.FarmlandWaterManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmBlock.class)
public class FarmBlockMixin {
    
    /**
     * 注入到isNearWater方法，让其也能识别盐水
     */
    @Inject(method = "isNearWater", at = @At("HEAD"), cancellable = true)
    private static void checkForBrine(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = level.getBlockState(pos);
        
        // 检查4x2x4范围内的流体
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            FluidState fluidState = level.getFluidState(blockpos);
            
            // 检查是否为盐水
            if (fluidState.getType() == GeographyFluids.BRINE.get().getSource() ||
                fluidState.getType() == GeographyFluids.BRINE.get().getFlowing()) {
                // 还需要检查是否可以被水合
                if (state.canBeHydrated(level, pos, fluidState, blockpos)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}