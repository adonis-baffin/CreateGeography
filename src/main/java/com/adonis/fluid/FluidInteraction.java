package com.adonis.fluid;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidInteractionRegistry;

public class FluidInteraction {


    public static void registerFluidInteractions() {

        FluidInteractionRegistry.addInteraction(GeographyFluids.BRINE.getType(), new FluidInteractionRegistry.InteractionInformation(
                ForgeMod.WATER_TYPE.get(),
                fluidState -> Blocks.OBSIDIAN.defaultBlockState()));
    }
}