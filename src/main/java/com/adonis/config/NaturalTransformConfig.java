package com.adonis.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class NaturalTransformConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // 全局设置
    public static final ForgeConfigSpec.BooleanValue ENABLE_NATURAL_TRANSFORMS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING;
    public static final ForgeConfigSpec.IntValue PROCESS_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue BLOCKS_PER_CHUNK_TICK;

    // 冰破裂设置
    public static final ForgeConfigSpec.BooleanValue ENABLE_ICE_CRACKING;
    public static final ForgeConfigSpec.BooleanValue ICE_REQUIRE_COLD_BIOME;
    public static final ForgeConfigSpec.BooleanValue ICE_REQUIRE_SKY_ACCESS;
    public static final ForgeConfigSpec.BooleanValue ICE_REQUIRE_RAW_ORE_BELOW;
    public static final ForgeConfigSpec.DoubleValue ICE_CRACK_BASE_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue ICE_CRACK_BOOST_PROBABILITY;
    public static final ForgeConfigSpec.IntValue ICE_CRACK_BOOST_RANGE;
    public static final ForgeConfigSpec.IntValue MAX_CRACKED_ICE_PER_CHUNK;

    // 冻土设置
    public static final ForgeConfigSpec.BooleanValue ENABLE_SOIL_FREEZING;
    public static final ForgeConfigSpec.BooleanValue SOIL_REQUIRE_COLD_BIOME;
    public static final ForgeConfigSpec.DoubleValue SOIL_FREEZE_BASE_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue SOIL_FREEZE_SURFACE_BOOST;
    public static final ForgeConfigSpec.DoubleValue SOIL_FREEZE_WATER_ICE_BOOST;
    public static final ForgeConfigSpec.IntValue SOIL_FREEZE_WATER_ICE_RANGE;
    public static final ForgeConfigSpec.IntValue MAX_FROZEN_SOIL_PER_CHUNK;

    // 盐碱化设置
    public static final ForgeConfigSpec.BooleanValue ENABLE_SALINIZATION;

    // 红树林沼泽盐碱化
    public static final ForgeConfigSpec.DoubleValue MANGROVE_SALINIZATION_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue MANGROVE_SALINIZATION_BOOST_PROBABILITY;
    public static final ForgeConfigSpec.IntValue MANGROVE_SALINIZATION_BOOST_RANGE;
    public static final ForgeConfigSpec.IntValue MAX_SALINE_BLOCKS_PER_CHUNK;

    // 盐水影响
    public static final ForgeConfigSpec.DoubleValue BRINE_SOIL_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue BRINE_MUD_PROBABILITY;
    public static final ForgeConfigSpec.IntValue BRINE_EFFECT_RANGE;

    // 灰水影响
    public static final ForgeConfigSpec.DoubleValue ASH_WATER_SOIL_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue ASH_WATER_MUD_PROBABILITY;
    public static final ForgeConfigSpec.IntValue ASH_WATER_EFFECT_RANGE;

    // 耕地转换设置
    public static final ForgeConfigSpec.BooleanValue ENABLE_FARMLAND_TRANSFORMS;
    public static final ForgeConfigSpec.DoubleValue FARMLAND_TO_BLACK_FARMLAND_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue FARMLAND_TO_SALINE_FARMLAND_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue BLACK_FARMLAND_TO_SALINE_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue BLACK_FARMLAND_FERTILITY_GAIN_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue SALINE_FARMLAND_SALINITY_GAIN_PROBABILITY;
    public static final ForgeConfigSpec.DoubleValue SALINE_FARMLAND_SALINITY_LOSS_PROBABILITY;
    public static final ForgeConfigSpec.IntValue FARMLAND_FLUID_CHECK_RANGE;

    static {
        BUILDER.comment("Create Geography Natural Transform Configuration");

        // 全局设置
        BUILDER.push("general");
        ENABLE_NATURAL_TRANSFORMS = BUILDER
                .comment("Enable all natural transformations")
                .define("enableNaturalTransforms", true);
        ENABLE_DEBUG_LOGGING = BUILDER
                .comment("Enable debug logging for transformations")
                .define("enableDebugLogging", false);
        PROCESS_INTERVAL_TICKS = BUILDER
                .comment("Interval in ticks between processing chunks (20 ticks = 1 second)")
                .defineInRange("processIntervalTicks", 200, 1, 1200);
        BLOCKS_PER_CHUNK_TICK = BUILDER
                .comment("Number of blocks to process per chunk section per tick")
                .defineInRange("blocksPerChunkTick", 3, 1, 20);
        BUILDER.pop();

        // 冰破裂设置
        BUILDER.push("iceCracking");
        ENABLE_ICE_CRACKING = BUILDER
                .comment("Enable ice cracking transformations")
                .define("enableIceCracking", false);
        ICE_REQUIRE_COLD_BIOME = BUILDER
                .comment("Require cold biome for ice cracking")
                .define("requireColdBiome", true);
        ICE_REQUIRE_SKY_ACCESS = BUILDER
                .comment("Require sky access for ice cracking")
                .define("requireSkyAccess", true);
        ICE_REQUIRE_RAW_ORE_BELOW = BUILDER
                .comment("Require raw ore block below for ice cracking")
                .define("requireRawOreBelow", true);
        ICE_CRACK_BASE_PROBABILITY = BUILDER
                .comment("Base probability for ice to crack (0.0 to 1.0)")
                .defineInRange("baseProbability", 0.02, 0.0, 1.0);
        ICE_CRACK_BOOST_PROBABILITY = BUILDER
                .comment("Additional probability when adjacent cracked ice exists")
                .defineInRange("boostProbability", 0.15, 0.0, 1.0);
        ICE_CRACK_BOOST_RANGE = BUILDER
                .comment("Range to check for adjacent cracked ice (X-axis)")
                .defineInRange("boostRange", 3, 1, 10);
        MAX_CRACKED_ICE_PER_CHUNK = BUILDER
                .comment("Maximum cracked ice blocks per chunk before stopping")
                .defineInRange("maxCrackedIcePerChunk", 50, 10, 500);
        BUILDER.pop();

        // 冻土设置
        BUILDER.push("soilFreezing");
        ENABLE_SOIL_FREEZING = BUILDER
                .comment("Enable soil freezing transformations")
                .define("enableSoilFreezing", true);
        SOIL_REQUIRE_COLD_BIOME = BUILDER
                .comment("Require cold biome for soil freezing")
                .define("requireColdBiome", true);
        SOIL_FREEZE_BASE_PROBABILITY = BUILDER
                .comment("Base probability for soil to freeze (0.0 to 1.0)")
                .defineInRange("baseProbability", 0.01, 0.0, 1.0);
        SOIL_FREEZE_SURFACE_BOOST = BUILDER
                .comment("Probability boost for surface blocks (no block above)")
                .defineInRange("surfaceBoost", 0.05, 0.0, 1.0);
        SOIL_FREEZE_WATER_ICE_BOOST = BUILDER
                .comment("Probability boost when adjacent to water or ice")
                .defineInRange("waterIceBoost", 0.1, 0.0, 1.0);
        SOIL_FREEZE_WATER_ICE_RANGE = BUILDER
                .comment("Range to check for water or ice")
                .defineInRange("waterIceRange", 2, 1, 5);
        MAX_FROZEN_SOIL_PER_CHUNK = BUILDER
                .comment("Maximum frozen soil blocks per chunk before stopping")
                .defineInRange("maxFrozenSoilPerChunk", 80, 20, 500);
        BUILDER.pop();

        // 盐碱化设置
        BUILDER.push("salinization");
        ENABLE_SALINIZATION = BUILDER
                .comment("Enable salinization transformations")
                .define("enableSalinization", true);

        // 红树林沼泽
        BUILDER.comment("Mangrove swamp salinization settings");
        MANGROVE_SALINIZATION_PROBABILITY = BUILDER
                .comment("Base probability for mud to become saline in mangrove swamps")
                .defineInRange("mangroveProbability", 0.005, 0.0, 1.0);
        MANGROVE_SALINIZATION_BOOST_PROBABILITY = BUILDER
                .comment("Additional probability when adjacent saline mud exists")
                .defineInRange("mangroveBoostProbability", 0.08, 0.0, 1.0);
        MANGROVE_SALINIZATION_BOOST_RANGE = BUILDER
                .comment("Range to check for adjacent saline mud (X-axis)")
                .defineInRange("mangroveBoostRange", 3, 1, 10);
        MAX_SALINE_BLOCKS_PER_CHUNK = BUILDER
                .comment("Maximum saline blocks per chunk before stopping (mangrove only)")
                .defineInRange("maxSalineBlocksPerChunk", 60, 20, 500);

        // 盐水影响
        BUILDER.comment("Brine fluid effect settings");
        BRINE_SOIL_PROBABILITY = BUILDER
                .comment("Probability for soil near brine to become saline")
                .defineInRange("brineSoilProbability", 0.3, 0.0, 1.0);
        BRINE_MUD_PROBABILITY = BUILDER
                .comment("Probability for mud near brine to become saline")
                .defineInRange("brineMudProbability", 0.5, 0.0, 1.0);
        BRINE_EFFECT_RANGE = BUILDER
                .comment("Range of brine effect")
                .defineInRange("brineEffectRange", 3, 1, 10);

        // 灰水影响
        BUILDER.comment("Ash water effect settings");
        ASH_WATER_SOIL_PROBABILITY = BUILDER
                .comment("Probability for soil near ash water to become black soil")
                .defineInRange("ashWaterSoilProbability", 0.25, 0.0, 1.0);
        ASH_WATER_MUD_PROBABILITY = BUILDER
                .comment("Probability for mud near ash water to become black mud")
                .defineInRange("ashWaterMudProbability", 0.4, 0.0, 1.0);
        ASH_WATER_EFFECT_RANGE = BUILDER
                .comment("Range of ash water effect")
                .defineInRange("ashWaterEffectRange", 3, 1, 10);

        // 耕地转换
        BUILDER.comment("Farmland transformation settings");
        ENABLE_FARMLAND_TRANSFORMS = BUILDER
                .comment("Enable farmland transformations")
                .define("enableFarmlandTransforms", true);
        FARMLAND_TO_BLACK_FARMLAND_PROBABILITY = BUILDER
                .comment("Probability for normal farmland to become black farmland near ash water")
                .defineInRange("farmlandToBlackFarmlandProbability", 0.1, 0.0, 1.0);
        FARMLAND_TO_SALINE_FARMLAND_PROBABILITY = BUILDER
                .comment("Probability for normal farmland to become saline farmland near brine")
                .defineInRange("farmlandToSalineFarmlandProbability", 0.15, 0.0, 1.0);
        BLACK_FARMLAND_TO_SALINE_PROBABILITY = BUILDER
                .comment("Probability for black farmland to become saline farmland near brine")
                .defineInRange("blackFarmlandToSalineProbability", 0.12, 0.0, 1.0);
        BLACK_FARMLAND_FERTILITY_GAIN_PROBABILITY = BUILDER
                .comment("Probability for black farmland to gain fertility near ash water")
                .defineInRange("blackFarmlandFertilityGainProbability", 0.08, 0.0, 1.0);
        SALINE_FARMLAND_SALINITY_GAIN_PROBABILITY = BUILDER
                .comment("Probability for saline farmland to gain salinity near brine")
                .defineInRange("salineFarmlandSalinityGainProbability", 0.1, 0.0, 1.0);
        SALINE_FARMLAND_SALINITY_LOSS_PROBABILITY = BUILDER
                .comment("Probability for saline farmland to lose salinity when only water/ash water nearby")
                .defineInRange("salineFarmlandSalinityLossProbability", 0.05, 0.0, 1.0);
        FARMLAND_FLUID_CHECK_RANGE = BUILDER
                .comment("Range to check for fluids around farmland")
                .defineInRange("farmlandFluidCheckRange", 4, 2, 8);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "creategeography-naturaltransforms.toml");
    }

    // 便捷访问方法
    public static boolean isNaturalTransformsEnabled() {
        return ENABLE_NATURAL_TRANSFORMS.get();
    }

    public static boolean isIceCrackingEnabled() {
        return isNaturalTransformsEnabled() && ENABLE_ICE_CRACKING.get();
    }

    public static boolean isSoilFreezingEnabled() {
        return isNaturalTransformsEnabled() && ENABLE_SOIL_FREEZING.get();
    }

    public static boolean isSalinizationEnabled() {
        return isNaturalTransformsEnabled() && ENABLE_SALINIZATION.get();
    }

    public static boolean isFarmlandTransformsEnabled() {
        return isNaturalTransformsEnabled() && ENABLE_FARMLAND_TRANSFORMS.get();
    }

    public static boolean isDebugLoggingEnabled() {
        return ENABLE_DEBUG_LOGGING.get();
    }
}