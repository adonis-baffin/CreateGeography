package com.adonis.datagen;

import com.adonis.CreateGeography;
import com.adonis.registry.ItemRegistry;
import com.adonis.tag.GeographyTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GeographyItemTagProvider extends ItemTagsProvider {
    
    public GeographyItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                   CompletableFuture<TagLookup<Block>> blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsProvider, CreateGeography.MODID, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        
        // ========== 模组内标签 ==========
        
        // 碎石材料
        tag(GeographyTags.CRUSHED_STONES)
                .add(ItemRegistry.CRUSHED_STONE.get())
                .add(ItemRegistry.CRUSHED_DEEP_SLATE.get());
        
        // 砾石类
        tag(GeographyTags.PEBBLES)
                .add(ItemRegistry.PEBBLE.get())
                .add(ItemRegistry.DEEP_SLATE_PEBBLE.get())
                .add(ItemRegistry.ORTHACLASE_PEBBLE.get())
                .add(ItemRegistry.PLAGIOCLASE_PEBBLE.get());
        
        // 粉末类
        tag(GeographyTags.POWDERS)
                .add(ItemRegistry.SAND_DUST.get())
                .add(ItemRegistry.RED_SAND_DUST.get())
                .add(ItemRegistry.MARBLE_POWDER.get())
                .add(ItemRegistry.PYROXENE_POWDER.get())
                .add(ItemRegistry.NITER_POWDER.get())
                .add(ItemRegistry.COAL_POWDER.get())
                .add(ItemRegistry.CHARCOAL_POWDER.get())
                .add(ItemRegistry.LAPIS_LAZULI_POWDER.get())
                .add(ItemRegistry.SULFUR_POWDER.get());
        
        // 抛光石材
        tag(GeographyTags.POLISHED_STONES)
                .add(ItemRegistry.POLISHED_HORNBLENDE.get())
                .add(ItemRegistry.POLISHED_PYROXENE.get());
        
        // 地质标本
        tag(GeographyTags.GEOLOGICAL_SPECIMENS)
                .add(ItemRegistry.ORTHACLASE.get())
                .add(ItemRegistry.PLAGIOCLASE.get())
                .add(ItemRegistry.HORNBLENDE.get())
                .add(ItemRegistry.PYROXENE.get())
                .add(ItemRegistry.BIOTITE.get())
                .add(ItemRegistry.MARBLE.get())
                .add(ItemRegistry.NITER.get());
        
        // 盐碱材料
        tag(GeographyTags.SALINE_MATERIALS)
                .add(ItemRegistry.SALT.get())
                .add(ItemRegistry.BRINE_BOTTLE.get())
                .add(ItemRegistry.SALINE_DIRT.get())
                .add(ItemRegistry.SALINE_MUD.get())
                .add(ItemRegistry.SALINE_FARMLAND.get());
        
        // ========== Forge 标签 ==========
        
        // 存储方块
        tag(GeographyTags.STORAGE_BLOCKS_ITEM_SALT)
                .add(ItemRegistry.SALT_BLOCK.get());
        
        // 各种粉末的具体分类
        tag(GeographyTags.DUSTS_SAND)
                .add(ItemRegistry.SAND_DUST.get());
        
        tag(GeographyTags.DUSTS_RED_SAND)
                .add(ItemRegistry.RED_SAND_DUST.get());
        
        tag(GeographyTags.DUSTS_COAL)
                .add(ItemRegistry.COAL_POWDER.get());
        
        tag(GeographyTags.DUSTS_CHARCOAL)
                .add(ItemRegistry.CHARCOAL_POWDER.get());
        
        tag(GeographyTags.DUSTS_LAPIS)
                .add(ItemRegistry.LAPIS_LAZULI_POWDER.get());
        
        tag(GeographyTags.DUSTS_SULFUR)
                .add(ItemRegistry.SULFUR_POWDER.get());
        
        tag(GeographyTags.DUSTS_MARBLE)
                .add(ItemRegistry.MARBLE_POWDER.get());
        
        tag(GeographyTags.DUSTS_PYROXENE)
                .add(ItemRegistry.PYROXENE_POWDER.get());
        
        tag(GeographyTags.DUSTS_NITER)
                .add(ItemRegistry.NITER_POWDER.get());
        
        tag(GeographyTags.DUSTS_ASH)
                .add(ItemRegistry.ASH.get());
        
        tag(GeographyTags.DUSTS_QUARTZ)
                .add(ItemRegistry.QUARTZ_SAND.get());
        
        // 宝石类
        tag(GeographyTags.GEMS_ORTHOCLASE)
                .add(ItemRegistry.ORTHACLASE.get());
        
        tag(GeographyTags.GEMS_PLAGIOCLASE)
                .add(ItemRegistry.PLAGIOCLASE.get());
        
        tag(GeographyTags.GEMS_HORNBLENDE)
                .add(ItemRegistry.HORNBLENDE.get());
        
        tag(GeographyTags.GEMS_PYROXENE)
                .add(ItemRegistry.PYROXENE.get());
        
        tag(GeographyTags.GEMS_BIOTITE)
                .add(ItemRegistry.BIOTITE.get());
        
        tag(GeographyTags.GEMS_MARBLE)
                .add(ItemRegistry.MARBLE.get());
        
        tag(GeographyTags.GEMS_NITER)
                .add(ItemRegistry.NITER.get());
        
        // 盐类
        tag(GeographyTags.SALTS)
                .add(ItemRegistry.SALT.get());
        
        tag(GeographyTags.SALTS_COMMON)
                .add(ItemRegistry.SALT.get());
        
        // 石质工具材料（砾石类）
        tag(GeographyTags.STONE_CRAFTING_MATERIALS)
                .add(ItemRegistry.PEBBLE.get())
                .add(ItemRegistry.DEEP_SLATE_PEBBLE.get())
                .add(ItemRegistry.ORTHACLASE_PEBBLE.get())
                .add(ItemRegistry.PLAGIOCLASE_PEBBLE.get());
        
        // 盐碱泥土
        tag(GeographyTags.DIRT_SALINE_ITEM)
                .add(ItemRegistry.SALINE_DIRT.get())
                .add(ItemRegistry.SALINE_MUD.get());
        
        // 盐碱耕地
        tag(GeographyTags.FARMLAND_SALINE_ITEM)
                .add(ItemRegistry.SALINE_FARMLAND.get());
        
        // 纤维
        tag(GeographyTags.FIBERS)
                .add(ItemRegistry.WOOD_FIBER.get());
        
        tag(GeographyTags.FIBERS_WOOD)
                .add(ItemRegistry.WOOD_FIBER.get());
        
        // 石英沙
        tag(GeographyTags.SAND_QUARTZ)
                .add(ItemRegistry.QUARTZ_SAND.get());
        
        // 盐水瓶
        tag(GeographyTags.BOTTLES_BRINE)
                .add(ItemRegistry.BRINE_BOTTLE.get());
        
        // ========== 原版/通用标签 ==========
        
        // 添加到原版标签
        tag(ItemTags.STONE_TOOL_MATERIALS)
                .addTag(GeographyTags.STONE_CRAFTING_MATERIALS);
    }
}