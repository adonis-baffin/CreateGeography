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
    public static final FluidEntry<ForgeFlowingFluid.Flowing> GREY_WATER =
            CreateGeography.REGISTRATE.fluid("grey_water",
                            new ResourceLocation(MODID, "block/fluid/grey_water_still"),
                            new ResourceLocation(MODID, "block/fluid/grey_water_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Grey Water")
                    .properties(b -> b.viscosity(1200)
                            .density(1000))
                    .fluidProperties(p -> p.levelDecreasePerBlock(3)
                            .tickRate(25)
                            .slopeFindDistance(2)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    // 泥浆
    public static final FluidEntry<ForgeFlowingFluid.Flowing> MUD =
            CreateGeography.REGISTRATE.fluid("mud",
                            new ResourceLocation(MODID, "block/fluid/mud_still"),
                            new ResourceLocation(MODID, "block/fluid/mud_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Mud")
                    .properties(b -> b.viscosity(2500)
                            .density(1400))
                    .fluidProperties(p -> p.levelDecreasePerBlock(1)
                            .tickRate(30)
                            .slopeFindDistance(2)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    // 沙浆
    public static final FluidEntry<ForgeFlowingFluid.Flowing> SAND_SLURRY =
            CreateGeography.REGISTRATE.fluid("sand_slurry",
                            new ResourceLocation(MODID, "block/fluid/sand_slurry_still"),
                            new ResourceLocation(MODID, "block/fluid/sand_slurry_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Sand Slurry")
                    .properties(b -> b.viscosity(2000)
                            .density(1300))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    // 鞣液
    public static final FluidEntry<ForgeFlowingFluid.Flowing> TANNIN =
            CreateGeography.REGISTRATE.fluid("tannin",
                            new ResourceLocation(MODID, "block/fluid/tannin_still"),
                            new ResourceLocation(MODID, "block/fluid/tannin_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Tannin")
                    .properties(b -> b.viscosity(1800)
                            .density(1200))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    // 电池废液
    public static final FluidEntry<ForgeFlowingFluid.Flowing> BATTERY_WASTE =
            CreateGeography.REGISTRATE.fluid("battery_waste",
                            new ResourceLocation(MODID, "block/fluid/battery_waste_still"),
                            new ResourceLocation(MODID, "block/fluid/battery_waste_flow"))
                    .source(ForgeFlowingFluid.Source::new)
                    .lang("Battery Waste")
                    .properties(b -> b.viscosity(1400)
                            .density(1300))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .bucket()
                    .build()
                    .register();

    public static void register() {
        // 这个方法在主类中调用，用于触发静态初始化
//        CreateGeography.LOGGER.info("Registering Geography fluids");
    }
}
