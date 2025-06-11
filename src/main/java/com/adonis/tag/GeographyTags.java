package com.adonis.tag;

import com.adonis.CreateGeography;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Create Geography 模组的标签定义
 * 包含模组内的标签和Forge通用标签
 */
public class GeographyTags {

    // ========== 模组内标签 ==========
    
    // 方块标签
    public static final TagKey<Block> SALINE_SOILS = modBlockTag("saline_soils");
    public static final TagKey<Block> GEOLOGICAL_MATERIALS = modBlockTag("geological_materials");
    
    // 物品标签
    public static final TagKey<Item> STONE_MATERIALS = modItemTag("stone_materials");
    public static final TagKey<Item> POWDERS = modItemTag("powders");
    public static final TagKey<Item> DUSTS = modItemTag("dusts");
    public static final TagKey<Item> PEBBLES = modItemTag("pebbles");
    public static final TagKey<Item> GEOLOGICAL_SPECIMENS = modItemTag("geological_specimens");
    public static final TagKey<Item> MINERAL_POWDERS = modItemTag("mineral_powders");
    public static final TagKey<Item> CRUSHED_STONES = modItemTag("crushed_stones");
    public static final TagKey<Item> POLISHED_STONES = modItemTag("polished_stones");
    public static final TagKey<Item> SALINE_MATERIALS = modItemTag("saline_materials");

    // ========== Forge 标签 ==========
    
    // 存储方块
    public static final TagKey<Block> STORAGE_BLOCKS_SALT = forgeBlockTag("storage_blocks/salt");
    public static final TagKey<Item> STORAGE_BLOCKS_ITEM_SALT = forgeItemTag("storage_blocks/salt");
    
    // 原材料和粉末
    public static final TagKey<Item> DUSTS_SAND = forgeItemTag("dusts/sand");
    public static final TagKey<Item> DUSTS_RED_SAND = forgeItemTag("dusts/red_sand");
    public static final TagKey<Item> DUSTS_COAL = forgeItemTag("dusts/coal");
    public static final TagKey<Item> DUSTS_CHARCOAL = forgeItemTag("dusts/charcoal");
    public static final TagKey<Item> DUSTS_LAPIS = forgeItemTag("dusts/lapis");
    public static final TagKey<Item> DUSTS_SULFUR = forgeItemTag("dusts/sulfur");
    public static final TagKey<Item> DUSTS_QUARTZ = forgeItemTag("dusts/quartz");
    public static final TagKey<Item> DUSTS_MARBLE = forgeItemTag("dusts/marble");
    public static final TagKey<Item> DUSTS_PYROXENE = forgeItemTag("dusts/pyroxene");
    public static final TagKey<Item> DUSTS_NITER = forgeItemTag("dusts/niter");
    public static final TagKey<Item> DUSTS_ASH = forgeItemTag("dusts/ash");
    
    // 宝石和矿物
    public static final TagKey<Item> GEMS_ORTHOCLASE = forgeItemTag("gems/orthoclase");
    public static final TagKey<Item> GEMS_PLAGIOCLASE = forgeItemTag("gems/plagioclase");
    public static final TagKey<Item> GEMS_HORNBLENDE = forgeItemTag("gems/hornblende");
    public static final TagKey<Item> GEMS_PYROXENE = forgeItemTag("gems/pyroxene");
    public static final TagKey<Item> GEMS_BIOTITE = forgeItemTag("gems/biotite");
    public static final TagKey<Item> GEMS_MARBLE = forgeItemTag("gems/marble");
    public static final TagKey<Item> GEMS_NITER = forgeItemTag("gems/niter");
    
    // 盐类
    public static final TagKey<Item> SALTS = forgeItemTag("salts");
    public static final TagKey<Item> SALTS_COMMON = forgeItemTag("salts/salt");
    
    // 工具材料
    public static final TagKey<Item> STONE_CRAFTING_MATERIALS = forgeItemTag("stone_tool_materials");
    
    // 泥土类型
    public static final TagKey<Block> DIRT_SALINE = forgeBlockTag("dirt/saline");
    public static final TagKey<Item> DIRT_SALINE_ITEM = forgeItemTag("dirt/saline");
    
    // 耕地
    public static final TagKey<Block> FARMLAND_SALINE = forgeBlockTag("farmland/saline");
    public static final TagKey<Item> FARMLAND_SALINE_ITEM = forgeItemTag("farmland/saline");
    
    // 纤维
    public static final TagKey<Item> FIBERS = forgeItemTag("fibers");
    public static final TagKey<Item> FIBERS_WOOD = forgeItemTag("fibers/wood");
    
    // 沙类
    public static final TagKey<Item> SAND_QUARTZ = forgeItemTag("sand/quartz");
    
    // 瓶装流体
    public static final TagKey<Item> BOTTLES_BRINE = forgeItemTag("bottles/brine");

    // ========== 工具方法 ==========
    
    private static TagKey<Block> modBlockTag(String path) {
        return BlockTags.create(new ResourceLocation(CreateGeography.MODID, path));
    }
    
    private static TagKey<Item> modItemTag(String path) {
        return ItemTags.create(new ResourceLocation(CreateGeography.MODID, path));
    }
    
    private static TagKey<Block> forgeBlockTag(String path) {
        return BlockTags.create(new ResourceLocation("forge", path));
    }

    private static TagKey<Item> forgeItemTag(String path) {
        return ItemTags.create(new ResourceLocation("forge", path));
    }
}