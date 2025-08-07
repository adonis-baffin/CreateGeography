package com.adonis.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class NaturalTransformConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // 冻土转换（只处理形成，融化由FrozenSoilBlock自己处理）
    public static final ForgeConfigSpec.BooleanValue ENABLE_FROZEN_SOIL_TRANSFORM;
    public static final ForgeConfigSpec.DoubleValue FROZEN_SOIL_TRANSFORM_CHANCE;
    public static final ForgeConfigSpec.IntValue FROZEN_SOIL_TRANSFORM_RANGE;

    // 盐水转换（直接转换，不包括传播）
    public static final ForgeConfigSpec.BooleanValue ENABLE_BRINE_TRANSFORM;
    public static final ForgeConfigSpec.DoubleValue BRINE_TRANSFORM_CHANCE;
    public static final ForgeConfigSpec.IntValue BRINE_TRANSFORM_RANGE;

    // 盐晶生成
    public static final ForgeConfigSpec.BooleanValue ENABLE_SALT_CRYSTAL_GENERATION;
    public static final ForgeConfigSpec.DoubleValue SALT_CRYSTAL_GENERATION_CHANCE;
    public static final ForgeConfigSpec.IntValue SALT_CRYSTAL_GENERATION_RANGE;

    // 硝化床
    public static final ForgeConfigSpec.BooleanValue ENABLE_NITER_BED_FORMATION;
    public static final ForgeConfigSpec.DoubleValue NITER_BED_FORMATION_CHANCE;
    public static final ForgeConfigSpec.IntValue NITER_BED_FORMATION_TIME;

    static {
        BUILDER.push("Natural Transform Config");

        // 冻土相关配置
        BUILDER.push("Frozen Soil");
        ENABLE_FROZEN_SOIL_TRANSFORM = BUILDER
                .comment("Enable frozen soil transformation from dirt near ice blocks")
                .define("enableFrozenSoilTransform", true);
        FROZEN_SOIL_TRANSFORM_CHANCE = BUILDER
                .comment("Chance for dirt to transform into frozen soil (0.0-1.0)")
                .defineInRange("frozenSoilTransformChance", 0.15, 0.0, 1.0);
        FROZEN_SOIL_TRANSFORM_RANGE = BUILDER
                .comment("Range to check for ice blocks around dirt")
                .defineInRange("frozenSoilTransformRange", 2, 1, 5);
        BUILDER.pop();

        // 盐水相关配置
        BUILDER.push("Brine");
        ENABLE_BRINE_TRANSFORM = BUILDER
                .comment("Enable brine direct transformation of nearby soil blocks and farmland")
                .define("enableBrineTransform", true);
        BRINE_TRANSFORM_CHANCE = BUILDER
                .comment("Chance for brine to directly transform nearby soil blocks into saline variants (0.0-1.0)")
                .defineInRange("brineTransformChance", 0.10, 0.0, 1.0);
        BRINE_TRANSFORM_RANGE = BUILDER
                .comment("Range for brine to directly affect nearby blocks (also used for desalination range)")
                .defineInRange("brineTransformRange", 4, 1, 5);
        BUILDER.pop();

        // 盐晶生成相关配置
        BUILDER.push("Salt Crystal");
        ENABLE_SALT_CRYSTAL_GENERATION = BUILDER
                .comment("Enable salt crystal generation on saline blocks")
                .define("enableSaltCrystalGeneration", true);
        SALT_CRYSTAL_GENERATION_CHANCE = BUILDER
                .comment("Chance for salt crystal to generate on saline soil (0.0-1.0)")
                .defineInRange("saltCrystalGenerationChance", 0.15, 0.0, 1.0);
        SALT_CRYSTAL_GENERATION_RANGE = BUILDER
                .comment("Range to check for brine around saline blocks for crystal generation")
                .defineInRange("saltCrystalGenerationRange", 5, 1, 8);
        BUILDER.pop();

        // 硝化床相关配置
        BUILDER.push("Niter Bed");
        ENABLE_NITER_BED_FORMATION = BUILDER
                .comment("Enable niter bed formation")
                .define("enableNiterBedFormation", true);
        NITER_BED_FORMATION_CHANCE = BUILDER
                .comment("Chance for niter bed to form niter crystals (0.0-1.0)")
                .defineInRange("niterBedFormationChance", 0.20, 0.0, 1.0);
        NITER_BED_FORMATION_TIME = BUILDER
                .comment("Time in ticks for niter bed to fully form niter")
                .defineInRange("niterBedFormationTime", 6000, 1200, 24000);
        BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}