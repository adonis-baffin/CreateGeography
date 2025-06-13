package com.adonis.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.adonis.content.block.BlackFarmlandBlock;
import static com.adonis.fluid.GeographyFluids.BRINE;
import static com.adonis.fluid.GeographyFluids.ASH_WATER;
import static com.adonis.registry.BlockRegistry.SALINE_FARMLAND;
import static com.adonis.registry.BlockRegistry.BLACK_FARMLAND;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {

    private static final IntegerProperty SALINITY = IntegerProperty.create("salinity", 0, 3);
    private static final IntegerProperty FERTILITY = IntegerProperty.create("fertility", 0, 3);

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        boolean nearBrine = isNearFluid(level, pos, BRINE.get().getSource());
        boolean nearAshWater = isNearFluid(level, pos, ASH_WATER.get().getSource());

        int currentMoisture = state.getValue(BlockStateProperties.MOISTURE);

        // 首先处理湿润逻辑 - 盐水也可以湿润普通耕地
        if (nearBrine || nearAshWater) {
            // 如果附近有盐水或灰水，且当前不是最大湿润度，先湿润耕地
            if (currentMoisture < 7) {
                level.setBlock(pos, state.setValue(BlockStateProperties.MOISTURE, 7), 2);
                currentMoisture = 7; // 更新本地变量
            }
        }

        // 然后处理转换逻辑
        // 盐水优先级最高 - 无论是普通耕地还是黑土耕地，都会被盐碱化
        if (nearBrine) {
            BlockState newState = SALINE_FARMLAND.get().defaultBlockState()
                    .setValue(BlockStateProperties.MOISTURE, currentMoisture); // 使用更新后的湿润度

            if (newState.hasProperty(SALINITY)) {
                newState = newState.setValue(SALINITY, 0); // 初始盐碱化等级为0
            }

            level.setBlock(pos, newState, 3);
            ci.cancel();
        }
        // 只有在没有盐水的情况下，灰水才能转换为黑土耕地
        else if (nearAshWater && !nearBrine) {
            BlockState newState = BLACK_FARMLAND.get().defaultBlockState()
                    .setValue(BlockStateProperties.MOISTURE, currentMoisture); // 使用更新后的湿润度

            if (newState.hasProperty(FERTILITY)) {
                newState = newState.setValue(FERTILITY, 3);
            }

            level.setBlock(pos, newState, 3);
            ci.cancel();
        }
    }

    private boolean isNearFluid(ServerLevel level, BlockPos pos, Fluid fluid) {
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            FluidState fluidState = level.getFluidState(checkPos);
            // 检查流体类型（包括源方块和流动方块）
            if (fluidState.getType() == fluid ||
                    (fluid == BRINE.get().getSource() && fluidState.getType() == BRINE.get().getFlowing()) ||
                    (fluid == ASH_WATER.get().getSource() && fluidState.getType() == ASH_WATER.get().getFlowing())) {
                return true;
            }
        }
        return false;
    }
}

// 新增：黑土耕地的Mixin，处理黑土耕地被盐碱化的情况
@Mixin(BlackFarmlandBlock.class)
class BlackFarmlandBlockMixin {

    private static final IntegerProperty SALINITY = IntegerProperty.create("salinity", 0, 3);

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        // 检查附近是否有盐水
        if (isNearFluid(level, pos, BRINE.get().getSource())) {
            int moisture = state.getValue(BlockStateProperties.MOISTURE);
            BlockState newState = SALINE_FARMLAND.get().defaultBlockState()
                    .setValue(BlockStateProperties.MOISTURE, moisture); // 保持原有湿润度

            if (newState.hasProperty(SALINITY)) {
                newState = newState.setValue(SALINITY, 0); // 初始盐碱化等级为0
            }

            level.setBlock(pos, newState, 3);
            ci.cancel(); // 取消原有的黑土耕地逻辑
        }
    }

    private boolean isNearFluid(ServerLevel level, BlockPos pos, Fluid fluid) {
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            FluidState fluidState = level.getFluidState(checkPos);
            // 检查流体类型（包括源方块和流动方块）
            if (fluidState.getType() == fluid ||
                    (fluid == BRINE.get().getSource() && fluidState.getType() == BRINE.get().getFlowing())) {
                return true;
            }
        }
        return false;
    }
}