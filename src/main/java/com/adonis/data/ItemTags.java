package com.adonis.data;

import com.adonis.CreateGeography;
import com.adonis.tag.ModTags;
import com.adonis.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ItemTags extends ItemTagsProvider {
    
    public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, blockTagProvider, CreateGeography.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 复制方块标签到物品标签
        copy(ModTags.STONE_CRAFTING_MATERIALS, ModTags.STONE_TOOL_MATERIALS_ITEM);
        copy(ModTags.SOIL_BLOCKS, ModTags.SOIL_BLOCKS_ITEM);
        copy(ModTags.DIRT_BLOCKS, ModTags.DIRT_BLOCKS_ITEM);
        
        // 矿物粉末标签
        tag(ModTags.MINERAL_POWDERS)
                .add(ItemRegistry.COAL_POWDER.get())
                .add(ItemRegistry.CHARCOAL_POWDER.get())
                .add(ItemRegistry.NITER_POWDER.get())
                .add(ItemRegistry.SULFUR_POWDER.get())
                .add(ItemRegistry.LAPIS_LAZULI_POWDER.get())
                .add(ItemRegistry.MARBLE_POWDER.get())
                .add(ItemRegistry.PYROXENE_POWDER.get())
                .add(ItemRegistry.ASH.get())
                .add(ItemRegistry.SAND_DUST.get())
                .add(ItemRegistry.RED_SAND_DUST.get());
    }
}