//package com.adonis.data;
//
//import com.adonis.CreateGeography;
//import com.adonis.tag.ModTags;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.data.PackOutput;
//import net.minecraftforge.common.data.BlockTagsProvider;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraftforge.common.data.ExistingFileHelper;
//
//import javax.annotation.Nullable;
//import java.util.concurrent.CompletableFuture;
//
//import static net.minecraft.tags.TagEntry.tag;
//
//public class BlockTags extends BlockTagsProvider {
//
//    public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
//        super(output, lookupProvider, CreateGeography.MODID, existingFileHelper);
//    }
//
//    @Override
//    protected void addTags(HolderLookup.Provider provider) {
//        // 石质工具材料标签
//        tag(ModTags.STONE_CRAFTING_MATERIALS)
//                .add(Blocks.GRAVEL)
//                .add(Blocks.COBBLED_DEEPSLATE);
//
//        // 土壤方块标签
//        tag(ModTags.SOIL_BLOCKS)
//                .add(Blocks.DIRT)
//                .add(Blocks.COARSE_DIRT)
//                .add(Blocks.ROOTED_DIRT)
//                .add(Blocks.PODZOL)
//                .add(Blocks.MYCELIUM)
//                .add(Blocks.GRASS_BLOCK)
//                .add(Blocks.DIRT_PATH);
//
//        // 泥土类方块标签
//        tag(ModTags.DIRT_BLOCKS)
//                .add(Blocks.DIRT)
//                .add(Blocks.COARSE_DIRT)
//                .add(Blocks.ROOTED_DIRT)
//                .add(Blocks.PODZOL)
//                .add(Blocks.MYCELIUM);
//    }
//}