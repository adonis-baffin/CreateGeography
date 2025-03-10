package com.adonis.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class BlockTransformUtils {
    // 自定义生物群系类别映射表（结合温度和路径判断）
    private static final Map<String, String> BIOME_CATEGORY_MAP = new HashMap<>();

    static {
        // 寒冷生物群系（icy）：基于温度和路径推断
        BIOME_CATEGORY_MAP.put("snowy_tundra", "icy");
        BIOME_CATEGORY_MAP.put("snowy_plains", "icy");
        BIOME_CATEGORY_MAP.put("snowy_slopes", "icy");
        BIOME_CATEGORY_MAP.put("snowy_mountains", "icy");
        BIOME_CATEGORY_MAP.put("frozen_ocean", "icy");
        BIOME_CATEGORY_MAP.put("frozen_river", "icy");
        BIOME_CATEGORY_MAP.put("ice_spikes", "icy");

        // 红树林沼泽（mangrove_swamp）直接映射
        BIOME_CATEGORY_MAP.put("mangrove_swamp", "mangrove_swamp");
    }

    /**
     * 检查附近是否有指定流体
     * @param level 世界对象
     * @param pos 检查位置
     * @param targetFluid 目标流体
     * @param range 检查范围（以格为单位）
     * @return 是否检测到目标流体
     */
    public static boolean isFluidNearby(LevelReader level, BlockPos pos, Fluid targetFluid, int range) {
        if (level == null || pos == null || targetFluid == null) {
            return false;
        }

        for (BlockPos checkPos : BlockPos.betweenClosed(
                pos.offset(-range, -1, -range), // 向下扩展一层以检测地下流体
                pos.offset(range, 1, range))) {
            BlockState blockState = level.getBlockState(checkPos);
            if (blockState.getFluidState().is(targetFluid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否在指定生物群系类别中
     * @param level 世界对象
     * @param pos 检查位置
     * @param categoryName 生物群系类别名称（如 "icy"）
     * @return 是否属于指定类别
     */
    public static boolean isInBiomeCategory(LevelReader level, BlockPos pos, String categoryName) {
        Holder<Biome> biomeHolder = level.getBiome(pos);
        if (!biomeHolder.isBound()) {
            return false;
        }
        Biome biome = biomeHolder.value();
        ResourceKey<Biome> biomeKey = biomeHolder.unwrapKey().orElse(null);
        if (biomeKey == null) {
            return false;
        }

        String biomePath = biomeKey.location().getPath().toLowerCase();
        if ("icy".equalsIgnoreCase(categoryName)) {
            if (biome.coldEnoughToSnow(pos)) {
                return BIOME_CATEGORY_MAP.getOrDefault(biomePath, "").equalsIgnoreCase(categoryName);
            }
            return false;
        }
        return BIOME_CATEGORY_MAP.getOrDefault(biomePath, "").equalsIgnoreCase(categoryName);
    }

    /**
     * 检查是否为特定生物群系
     * @param level 世界对象
     * @param pos 检查位置
     * @param biomeName 生物群系名称（如 "mangrove_swamp"）
     * @return 是否为指定生物群系
     */
    public static boolean isSpecificBiome(LevelReader level, BlockPos pos, String biomeName) {
        Holder<Biome> biomeHolder = level.getBiome(pos);
        if (!biomeHolder.isBound()) {
            return false;
        }
        ResourceKey<Biome> biomeKey = biomeHolder.unwrapKey().orElse(null);
        if (biomeKey == null) {
            return false;
        }
        return biomeKey.location().getPath().equalsIgnoreCase(biomeName);
    }
}