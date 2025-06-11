package com.adonis.datagen;

import com.adonis.CreateGeography;
import com.adonis.registry.BlockRegistry;
import com.adonis.tag.GeographyTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GeographyBlockTagProvider extends BlockTagsProvider {
    
    public GeographyBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, 
                                   @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreateGeography.MODID, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        
        // ========== 模组内标签 ==========
        
        // 盐碱土壤
        tag(GeographyTags.SALINE_SOILS)
                .add(BlockRegistry.SALINE_DIRT.get())
                .add(BlockRegistry.SALINE_MUD.get())
                .add(BlockRegistry.SALINE_FARMLAND.get());
        
        // ========== Forge 标签 ==========
        
        // 存储方块
        tag(GeographyTags.STORAGE_BLOCKS_SALT)
                .add(BlockRegistry.SALT_BLOCK.get());
        
        // 盐碱泥土
        tag(GeographyTags.DIRT_SALINE)
                .add(BlockRegistry.SALINE_DIRT.get())
                .add(BlockRegistry.SALINE_MUD.get());
        
        // 盐碱耕地
        tag(GeographyTags.FARMLAND_SALINE)
                .add(BlockRegistry.SALINE_FARMLAND.get());
        
        // ========== 工具挖掘标签 ==========
        
        // 铲子挖掘
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(BlockRegistry.SALINE_DIRT.get())
                .add(BlockRegistry.SALINE_MUD.get())
                .add(BlockRegistry.SALINE_FARMLAND.get());
        
        // 镐子挖掘
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BlockRegistry.SALT_BLOCK.get());
        
        // ========== 原版标签 ==========
        
        // 泥土类方块
        tag(BlockTags.DIRT)
                .add(BlockRegistry.SALINE_DIRT.get());
    }
}