//package com.adonis.fluid;
//
//import net.minecraft.world.level.block.Blocks;
//import net.minecraftforge.common.ForgeMod;
//import net.minecraftforge.fluids.FluidInteractionRegistry;
//
//public class ChromaticFluidInteraction {
//
//
//    public static void registerFluidInteractions() {
//
//        FluidInteractionRegistry.addInteraction(GeographyFluids.CHROMATIC_WASTE.getType(), new FluidInteractionRegistry.InteractionInformation(
//                ForgeMod.WATER_TYPE.get(),
//                fluidState -> Blocks.OBSIDIAN.defaultBlockState()));
//
//        FluidInteractionRegistry.addInteraction(DDFluids.CHROMATIC_WASTE.getType(), new FluidInteractionRegistry.InteractionInformation(
//                ForgeMod.LAVA_TYPE.get(),
//                fluidState -> Blocks.CRYING_OBSIDIAN.defaultBlockState()));
//    }
//}
