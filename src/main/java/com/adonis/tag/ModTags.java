package com.adonis.tag;

import com.adonis.CreateGeography;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    
    // 方块标签
    public static final TagKey<Block> STONE_CRAFTING_MATERIALS = modBlockTag("stone_tool_materials");
    public static final TagKey<Block> SOIL_BLOCKS = modBlockTag("soil_blocks");
    public static final TagKey<Block> DIRT_BLOCKS = modBlockTag("dirt_blocks");
    
    // 物品标签
    public static final TagKey<Item> STONE_TOOL_MATERIALS_ITEM = modItemTag("stone_tool_materials");
    public static final TagKey<Item> MINERAL_POWDERS = modItemTag("mineral_powders");
    public static final TagKey<Item> SOIL_BLOCKS_ITEM = modItemTag("soil_blocks");
    public static final TagKey<Item> DIRT_BLOCKS_ITEM = modItemTag("dirt_blocks");
    
    private static TagKey<Block> modBlockTag(String path) {
        return BlockTags.create(new ResourceLocation(CreateGeography.MODID, path));
    }
    
    private static TagKey<Item> modItemTag(String path) {
        return ItemTags.create(new ResourceLocation(CreateGeography.MODID, path));
    }
}