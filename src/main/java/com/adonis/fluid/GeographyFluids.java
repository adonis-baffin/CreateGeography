package com.adonis.fluid;

import com.adonis.CreateGeography;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;


import static com.adonis.CreateGeography.REGISTRATE;

public class GeographyFluids {

    public static final FluidEntry<ForgeFlowingFluid.Flowing> PLANT_OIL =
            REGISTRATE.fluid("plant_oil", new ResourceLocation("createdieselgenerators:block/plant_oil_still"), new ResourceLocation("createdieselgenerators:block/plant_oil_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Plant Oil")
                    .properties(b -> b.viscosity(1500)
                            .density(500))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();
    public static final FluidEntry<ForgeFlowingFluid.Flowing> CRUDE_OIL =
            REGISTRATE.fluid("crude_oil", new ResourceLocation("createdieselgenerators:block/crude_oil_still"), new ResourceLocation("createdieselgenerators:block/crude_oil_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Crude Oil")
                    .properties(b -> b.viscosity(1500)
                            .density(100))
                    .fluidProperties(p -> p.levelDecreasePerBlock(3)
                            .tickRate(25)
                            .slopeFindDistance(2)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> BIODIESEL =
            REGISTRATE.fluid("biodiesel", new ResourceLocation("createdieselgenerators:block/biodiesel_still"), new ResourceLocation("createdieselgenerators:block/biodiesel_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Biodiesel")
                    .properties(b -> b.viscosity(1500)
                            .density(500))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();
    public static final FluidEntry<ForgeFlowingFluid.Flowing> DIESEL =
            REGISTRATE.fluid("diesel", new ResourceLocation("createdieselgenerators:block/diesel_still"), new ResourceLocation("createdieselgenerators:block/diesel_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Diesel")
                    .properties(b -> b.viscosity(1500)
                            .density(500))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();
    public static final FluidEntry<ForgeFlowingFluid.Flowing> GASOLINE =
            REGISTRATE.fluid("gasoline", new ResourceLocation("createdieselgenerators:block/gasoline_still"), new ResourceLocation("createdieselgenerators:block/gasoline_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Gasoline")
                    .properties(b -> b.viscosity(1500)
                            .density(500))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();
    public static final FluidEntry<ForgeFlowingFluid.Flowing> ETHANOL =
            REGISTRATE.fluid("ethanol", new ResourceLocation("createdieselgenerators:block/ethanol_still"), new ResourceLocation("createdieselgenerators:block/ethanol_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Ethanol")
                    .properties(b -> b.viscosity(1500)
                            .density(500))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(5)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();


    public static void register() {}


}