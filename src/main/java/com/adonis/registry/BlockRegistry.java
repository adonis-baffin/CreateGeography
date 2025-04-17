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

    // 破裂方块
    public static final RegistryObject<Block> CRACKED_BASALT = BLOCKS.register("cracked_basalt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BASALT)));

    public static final RegistryObject<Block> CRACKED_BLACKSTONE = BLOCKS.register("cracked_blackstone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLACKSTONE)));

    public static final RegistryObject<Block> CRACKED_ANDESITE = BLOCKS.register("cracked_andesite",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));

    public static final RegistryObject<Block> CRACKED_GRANITE = BLOCKS.register("cracked_granite",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GRANITE)));

    public static final RegistryObject<Block> CRACKED_DIORITE = BLOCKS.register("cracked_diorite",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIORITE)));

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

    public static final RegistryObject<Block> CRACKED_PACKED_ICE = BLOCKS.register("cracked_packed_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(0.2f, 0.2f)
                    .sound(SoundType.GLASS)
                    .friction(0.98f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(PACKED_ICE_SHARDS.get(), builder.getLevel().random.nextInt(2, 4)));
                }
            });

    public static final RegistryObject<Block> CRACKED_BLUE_ICE = BLOCKS.register("cracked_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(1.2f, 1.2f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)) {
                @Override
                public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
                    return Collections.singletonList(new ItemStack(BLUE_ICE_SHARDS.get(), builder.getLevel().random.nextInt(2, 4)));
                }
            });

    // 土壤相关方块
    public static final RegistryObject<Block> FROZEN_SOIL = BLOCKS.register("frozen_soil",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5f, 0.5f)
                    .sound(SoundType.GRAVEL)
                    .randomTicks()));

    public static final RegistryObject<Block> SALINE_DIRT = BLOCKS.register("saline_soil",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5f, 0.5f)
                    .sound(SoundType.GRAVEL)
                    .randomTicks()));

    public static final RegistryObject<Block> SALINE_MUD = BLOCKS.register("saline_mud",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.6f, 0.6f)
                    .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> SALINE_FARMLAND = BLOCKS.register("saline_farmland",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.6f, 0.6f)
                    .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> BLACK_DIRT = BLOCKS.register("black_soil",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BLACK)
                    .strength(0.5f, 0.5f)
                    .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> BLACK_MUD = BLOCKS.register("black_mud",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BLACK)
                    .strength(0.5f, 0.5f)
                    .sound(SoundType.SLIME_BLOCK)));

    public static final RegistryObject<Block> BLACK_FARMLAND = BLOCKS.register("black_farmland",
            () -> new BlackFarmlandBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BLACK)
                    .strength(0.6f, 0.6f)
                    .sound(SoundType.GRAVEL)));

    // 堆积物方块
    public static final RegistryObject<Block> SAND_PILE = BLOCKS.register("sand_pile",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5f, 0.5f)
                    .sound(SoundType.SAND)));

    public static final RegistryObject<Block> SNOW_PILE = BLOCKS.register("snow_pile",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(0.2f, 0.2f)
                    .sound(SoundType.SNOW)));

    public static final RegistryObject<Block> ASH_PILE = BLOCKS.register("ash_pile",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.6f, 0.6f)
                    .sound(SoundType.GRAVEL)));

    // 石材方块
    public static final RegistryObject<Block> PEBBLE_BLOCK = BLOCKS.register("pebble_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.8f)
                    .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> QUARTZITE = BLOCKS.register("quartzite",
            () -> new Block(stoneBlock().mapColor(MapColor.QUARTZ)));

    public static final RegistryObject<Block> MARBLE_BLOCK = BLOCKS.register("marble_block",
            () -> new Block(stoneBlock().mapColor(MapColor.SNOW)));

    public static final RegistryObject<Block> PEBBLE_PATH = BLOCKS.register("pebble_path",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.8f, 14.0f)
                    .speedFactor(1.3f)
                    .jumpFactor(1.1f)
                    .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> QUARTZITE_PATH = BLOCKS.register("quartzite_path",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.8f, 14.0f)
                    .speedFactor(1.3f)
                    .jumpFactor(1.1f)
                    .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> MARBLE_PATH = BLOCKS.register("marble_path",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.8f, 14.0f)
                    .speedFactor(1.3f)
                    .jumpFactor(1.1f)
                    .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> PARENT_MATERIAL = BLOCKS.register("parent_material",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5f)
                    .sound(SoundType.GRAVEL)));

    // 玻璃与晶体
    public static final RegistryObject<Block> PYROXENE_GLASS = BLOCKS.register("pyroxene_glass",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(1.5f, 6.0f)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, entity) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)));

    public static final RegistryObject<Block> SALT_CRYSTAL = BLOCKS.register("salt_crystal",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(0.8f)
                    .sound(SoundType.GLASS)));

    // 木框方块
    public static final RegistryObject<Block> WOODEN_FRAME = BLOCKS.register("wooden_frame",
            () -> new ThinFrameBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.5f)
                    .sound(SoundType.BAMBOO_WOOD)));

    public static final RegistryObject<Block> WATER_FILLED_WOODEN_FRAME = BLOCKS.register("water_filled_wooden_frame",
            () -> new FilledWoodenFrameBlock(BlockBehaviour.Properties.copy(WOODEN_FRAME.get())
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)));

    public static final RegistryObject<Block> BRINE_FILLED_WOODEN_FRAME = BLOCKS.register("brine_filled_wooden_frame",
            () -> new FilledWoodenFrameBlock(BlockBehaviour.Properties.copy(WOODEN_FRAME.get())
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .randomTicks()));

    public static final RegistryObject<Block> SALT_FILLED_WOODEN_FRAME = BLOCKS.register("salt_filled_wooden_frame",
            () -> new SaltFilledWoodenFrameBlock(BlockBehaviour.Properties.copy(WOODEN_FRAME.get())
                    .mapColor(MapColor.STONE)));

    public static final RegistryObject<Block> MUD_FILLED_WOODEN_FRAME = BLOCKS.register("mud_filled_wooden_frame",
            () -> new FilledWoodenFrameBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.5f)
                    .sound(SoundType.BAMBOO_WOOD)
                    .randomTicks()));

    public static final RegistryObject<Block> SAND_SLURRY_FILLED_WOODEN_FRAME = BLOCKS.register("sand_slurry_filled_wooden_frame",
            () -> new FilledWoodenFrameBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.5f)
                    .sound(SoundType.BAMBOO_WOOD)
                    .randomTicks()));

    public static final RegistryObject<Block> DIRT_CLOD_FILLED_WOODEN_FRAME = BLOCKS.register("dirt_clods_filled_wooden_frame",
            () -> new DirtClodFilledWoodenFrameBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5f)
                    .sound(SoundType.BAMBOO_WOOD)));

    public static final RegistryObject<Block> SAND_DUST_FILLED_WOODEN_FRAME = BLOCKS.register("sand_dust_filled_wooden_frame",
            () -> new SandDustFilledWoodenFrameBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5f)
                    .sound(SoundType.BAMBOO_WOOD)));

    // 其他功能方块
    public static final RegistryObject<Block> TRUE_ICE = BLOCKS.register("true_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.5f)
                    .friction(0.995f)
                    .sound(SoundType.GLASS)));

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

    public static final RegistryObject<Block> ANDESITE_FIRE_PIT = BLOCKS.register("andesite_fire_pit",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.5f)
                    .lightLevel(state -> 15)
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> BRASS_FIRE_PIT = BLOCKS.register("brass_fire_pit",
            () -> new Block(BlockBehaviour.Properties.copy(ANDESITE_FIRE_PIT.get())
                    .mapColor(MapColor.METAL)));

    public static final RegistryObject<Block> MECHANICAL_FISHING_NET = BLOCKS.register("mechanical_fishing_net",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static final RegistryObject<Block> INDUSTRIAL_COMPOSTER = BLOCKS.register("industrial_composter",
            () -> new IndustrialComposterBlock(BlockBehaviour.Properties.copy(Blocks.COMPOSTER)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<Block> INDUSTRIAL_ANVIL = BLOCKS.register("industrial_anvil",
            () -> new IndustrialAnvilBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL)));

    public static final RegistryObject<Block> INDUSTRIAL_FURNACE = BLOCKS.register("industrial_furnace",
            () -> new IndustrialFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.FURNACE)));

    public static final RegistryObject<Block> ELECTRIC_BURNER = BLOCKS.register("electric_burner",
            () -> new ElectricBurnerBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    // Registrate 注册的方块
    public static final BlockEntry<PyroxeneHeaterBlock> PYROXENE_HEATER =
            CreateGeography.REGISTRATE
                    .block("pyroxene_heater", PyroxeneHeaterBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.strength(2.0F).requiresCorrectToolForDrops())
                    .blockstate((ctx, prov) -> {
                        prov.simpleBlock(ctx.get(), prov.models()
                                .cubeAll(ctx.getName(), prov.modLoc("block/pyroxene_heater")));
                    })
                    .transform(TagGen.axeOrPickaxe())
                    .item()
                    .transform(ModelGen.customItemModel())
                    .register();

    public static final BlockEntry<PyroxeneMirrorBlock> PYROXENE_MIRROR = CreateGeography.REGISTRATE
            .block("pyroxene_mirror", PyroxeneMirrorBlock::new)
            .properties(p -> p
                    .mapColor(MapColor.STONE)
                    .strength(2.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
            .blockstate((ctx, prov) -> {
                prov.directionalBlock(ctx.get(), prov.models()
                        .withExistingParent(ctx.getName(), "creategeography:block/encased_mirror/mirror"));
            })
            .transform(TagGen.axeOrPickaxe())
            .item()
            .transform(ModelGen.customItemModel())
            .register();
}