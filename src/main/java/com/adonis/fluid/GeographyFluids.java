package com.adonis.fluid;

import com.adonis.CreateGeography;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import com.tterrag.registrate.util.entry.FluidEntry;

import static com.adonis.CreateGeography.MODID;

public class GeographyFluids {

    // 盐水
    public static final FluidEntry<ForgeFlowingFluid.Flowing> BRINE =
            CreateGeography.REGISTRATE.fluid("brine",
                            new ResourceLocation(MODID, "block/fluid/brine_still"),
                            new ResourceLocation(MODID, "block/fluid/brine_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Brine")
                    .properties(b -> b.viscosity(1500)
                            .density(1100))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    // 灰水
    public static final FluidEntry<ForgeFlowingFluid.Flowing> ASH_WATER =
            CreateGeography.REGISTRATE.fluid("ash_water",
                            new ResourceLocation(MODID, "block/fluid/ash_water_still"),
                            new ResourceLocation(MODID, "block/fluid/ash_water_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Ash Water")
                    .properties(b -> b.viscosity(1200)
                            .density(1050))
                    .fluidProperties(p -> p.levelDecreasePerBlock(1)
                            .tickRate(20)
                            .slopeFindDistance(4)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    public static void register() {
    }
}