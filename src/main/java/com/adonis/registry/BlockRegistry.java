package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.block.*;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.List;

import static com.adonis.registry.ItemRegistry.*;
import static com.adonis.CreateGeography.REGISTRATE;

public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreateGeography.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreateGeography.MODID);

    // 基础方块属性模板
    private static BlockBehaviour.Properties stoneBlock() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(1.5f, 6.0f)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties iceBlock() {
        return BlockBehaviour.Properties.of()
                .friction(0.98f)
                .sound(SoundType.GLASS)
                .strength(1.2f)
                .requiresCorrectToolForDrops();
    }

    // === 使用CreateRegistrate注册的需要tooltip的方块 ===

    public static final BlockEntry<NiterBedBlock> NITER_BED = REGISTRATE
            .block("niter_bed", NiterBedBlock::new)
            .initialProperties(() -> Blocks.SAND)
            .properties(prop -> prop
                    .mapColor(MapColor.COLOR_YELLOW)
                    .randomTicks()
                    .strength(0.8f)
                    .sound(SoundType.SAND)
                    .requiresCorrectToolForDrops())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get()))
            .simpleItem()
            .register();

    public static final BlockEntry<SalineMudBlock> SALINE_MUD = REGISTRATE
            .block("saline_mud", SalineMudBlock::new)
            .initialProperties(() -> Blocks.MUD)
            .properties(prop -> prop
                    .mapColor(MapColor.DIRT)
                    .randomTicks()
                    .strength(0.6f, 0.6f)
                    .sound(SoundType.GRAVEL))
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get()))
            .simpleItem()
            .register();

    public static final BlockEntry<SalineDirtBlock> SALINE_DIRT = REGISTRATE
            .block("saline_dirt", SalineDirtBlock::new)
            .initialProperties(() -> Blocks.DIRT)
            .properties(prop -> prop
                    .mapColor(MapColor.DIRT)
                    .strength(0.5f, 0.5f)
                    .sound(SoundType.GRAVEL)
                    .randomTicks())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get()))
            .simpleItem()
            .register();

    public static final BlockEntry<SalineFarmlandBlock> SALINE_FARMLAND = REGISTRATE
            .block("saline_farmland", SalineFarmlandBlock::new)
            .initialProperties(() -> Blocks.FARMLAND)
            .properties(prop -> prop
                    .mapColor(MapColor.DIRT)
                    .randomTicks()
                    .strength(0.6f)
                    .sound(SoundType.GRAVEL))
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get()))
            .simpleItem()
            .register();

    public static final BlockEntry<FrozenSoilBlock> FROZEN_SOIL = REGISTRATE
            .block("frozen_soil", FrozenSoilBlock::new)
            .initialProperties(() -> Blocks.DIRT)
            .properties(prop -> prop
                    .mapColor(MapColor.DIRT)
                    .randomTicks()
                    .strength(0.6f, 0.6f)
                    .sound(SoundType.GRAVEL))
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get()))
            .simpleItem()
            .register();

    // === 使用传统DeferredRegister注册的不需要tooltip的方块 ===

    // 含矿蓝冰方块
    public static final RegistryObject<Block> IRON_BEARING_BLUE_ICE = BLOCKS.register("iron_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(IRON_ORE_POWDER.get(), builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> COPPER_BEARING_BLUE_ICE = BLOCKS.register("copper_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(COPPER_ORE_POWDER.get(), builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> GOLD_BEARING_BLUE_ICE = BLOCKS.register("gold_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(GOLD_ORE_POWDER.get(), builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> COAL_BEARING_BLUE_ICE = BLOCKS.register("coal_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(COAL_POWDER.get(), builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> LAPIS_LAZULI_BEARING_BLUE_ICE = BLOCKS.register("lapis_lazuli_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.LAPIS)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(LAPIS_LAZULI_POWDER.get(), builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> REDSTONE_BEARING_BLUE_ICE = BLOCKS.register("redstone_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(net.minecraft.world.item.Items.REDSTONE, builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> ZINC_BEARING_BLUE_ICE = BLOCKS.register("zinc_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(ZINC_ORE_POWDER.get(), builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> OSMIUM_BEARING_BLUE_ICE = BLOCKS.register("osmium_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(OSMIUM_ORE_POWDER.get(), builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> SALT_BEARING_BLUE_ICE = BLOCKS.register("salt_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(SALT.get(), builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> NITER_BEARING_BLUE_ICE = BLOCKS.register("niter_bearing_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(2.8f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(NITER_POWDER.get(), builder.getLevel().random.nextInt(1, 3)));
                }
            });

    public static final RegistryObject<Block> CRACKED_ICE = BLOCKS.register("cracked_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(0.2f, 0.2f)
                    .sound(SoundType.GLASS)
                    .friction(0.98f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(ICE_SHARDS.get(), builder.getLevel().random.nextInt(2, 4)));
                }
            });

    public static final RegistryObject<Block> SALT_CRYSTAL = BLOCKS.register("salt_crystal",
            () -> new SaltCrystalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(0.3F)
                    .sound(SoundType.SNOW)
                    .dynamicShape()
            ));

    public static final RegistryObject<Block> NITER_CRYSTAL = BLOCKS.register("niter_crystal",
            () -> new NiterCrystalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(0.3F)
                    .sound(SoundType.SNOW)
                    .dynamicShape()
            ));

    // 木框方块
    public static final RegistryObject<Block> WOODEN_FRAME = BLOCKS.register("wooden_frame",
            () -> new ThinFrameBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.5f)
                    .sound(SoundType.BAMBOO_WOOD)));

    public static final RegistryObject<Block> SALT_BLOCK = BLOCKS.register("salt_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(1.2f, 1.2f)
                    .sound(SoundType.SAND)));

    public static final RegistryObject<Block> NITER_BLOCK = BLOCKS.register("niter_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(1.2f, 1.2f)
                    .sound(SoundType.NETHERRACK)));
}