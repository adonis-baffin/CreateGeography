package com.adonis.fluid;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidInteractionRegistry;

public class FluidInteraction {

    public static void registerFluidInteractions() {

        // --- 卤水 (Brine) 与 岩浆 (Lava) 的交互 ---

        // 1. 当 卤水 流到 岩浆 上时 -> 生成 哭泣的黑曜石
        FluidInteractionRegistry.addInteraction(GeographyFluids.BRINE.getType(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.LAVA_TYPE.get(),
                fluidState -> Blocks.CRYING_OBSIDIAN.defaultBlockState()));

        // 2. 当 岩浆 流到 卤水 上时 -> 生成 石头/圆石 (模仿原版行为)
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                GeographyFluids.BRINE.getType(),
                fluidState -> {
                    // 检查卤水是否是源方块
                    if (fluidState.isSource()) {
                        return Blocks.STONE.defaultBlockState(); // 如果是源，生成石头
                    }
                    return Blocks.COBBLESTONE.defaultBlockState(); // 如果是流动的，生成圆石
                }));


        // --- 灰烬水 (Ash Water) 与 岩浆 (Lava) 的交互 ---

        // 1. 当 灰烬水 流到 岩浆 上时 -> 生成 黑曜石
        FluidInteractionRegistry.addInteraction(GeographyFluids.ASH_WATER.getType(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.LAVA_TYPE.get(),
                fluidState -> Blocks.OBSIDIAN.defaultBlockState()));

        // 2. 当 岩浆 流到 灰烬水 上时 -> 生成 石头/圆石
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                GeographyFluids.ASH_WATER.getType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.STONE.defaultBlockState();
                    }
                    return Blocks.COBBLESTONE.defaultBlockState();
                }));
    }
}