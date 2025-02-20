package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.block.ElectricBurnerBlock;
import com.adonis.content.block.entity.ElectricBurnerBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreateGeography.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreateGeography.MODID);
    // 基础方块属性模板（石头类型）
    private static BlockBehaviour.Properties stoneBlock() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(1.5f, 6.0f)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops();
    }

    // 注册所有方块
    public static final RegistryObject<Block> SAND_PILE = BLOCKS.register("sand_pile",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5f, 0.5f)
                    .sound(SoundType.SAND)
            ));

    public static final RegistryObject<Block> SNOW_PILE = BLOCKS.register("snow_pile",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(0.2f, 0.2f)
                    .sound(SoundType.SNOW)
            ));

    public static final RegistryObject<Block> ASH_PILE = BLOCKS.register("ash_pile",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.6f, 0.6f)
                    .sound(SoundType.GRAVEL)
            ));

    public static final RegistryObject<Block> QUARTZITE = BLOCKS.register("quartzite",
            () -> new Block(stoneBlock().mapColor(MapColor.QUARTZ)));

    public static final RegistryObject<Block> MARBLE_BLOCK = BLOCKS.register("marble_block",
            () -> new Block(stoneBlock().mapColor(MapColor.SNOW)));

    public static final RegistryObject<Block> PEBBLE_PATH = BLOCKS.register("pebble_path",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.8f, 14.0f)
                    .speedFactor(1.1f)
                    .jumpFactor(1.1f)
                    .sound(SoundType.GRAVEL)
            ));

    public static final RegistryObject<Block> QUARTZITE_PATH = BLOCKS.register("quartzite_path",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.8f, 14.0f)
                    .speedFactor(1.1f)
                    .jumpFactor(1.1f)
                    .sound(SoundType.GRAVEL)
            ));

    public static final RegistryObject<Block> MARBLE_PATH = BLOCKS.register("marble_path",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.8f, 14.0f)
                    .speedFactor(1.1f)
                    .jumpFactor(1.1f)
                    .sound(SoundType.GRAVEL)
            ));

    public static final RegistryObject<Block> PYROXENE_GLASS = BLOCKS.register("pyroxene_glass",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(1.5f,6.0f)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, entity) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)
            ));

    public static final RegistryObject<Block> ELECTRIC_BURNER = BLOCKS.register("electric_burner",
            () -> new ElectricBurnerBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> SOLAR_HEATER = BLOCKS.register("solar_heater",
            () -> new Block(stoneBlock().mapColor(MapColor.COLOR_GRAY)));

    public static final RegistryObject<Block> CRACKED_ICE = BLOCKS.register("cracked_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(0.2f,0.2f)
                    .sound(SoundType.GLASS)
                    .friction(0.98f)
            ));

    public static final RegistryObject<Block> CRACKED_PACKED_ICE = BLOCKS.register("cracked_packed_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(0.2f,0.2f)
                    .sound(SoundType.GLASS)
                    .friction(0.98f)
            ));

    public static final RegistryObject<Block> CRACKED_BLUE_ICE = BLOCKS.register("cracked_blue_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(1.2f,1.2f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)
            ));

    public static final RegistryObject<Block> ORE_BEARING_GLACIAL_ICE = BLOCKS.register("ore_bearing_glacial_ice",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(1.2f,1.2f)
                    .sound(SoundType.GLASS)
                    .friction(0.989f)
            ));

    public static final RegistryObject<Block> PERMAFROST = BLOCKS.register("permafrost",
            () -> new Block(stoneBlock().mapColor(MapColor.STONE)));

    public static final RegistryObject<Block> SALT_BLOCK = BLOCKS.register("salt_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(1.2f,1.2f)
                    .sound(SoundType.SAND)
            ));

    public static final RegistryObject<Block> SALINE_SOIL = BLOCKS.register("saline_soil",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5f,0.5f)
                    .sound(SoundType.GRAVEL)
            ));

    public static final RegistryObject<Block> SALINE_MUD = BLOCKS.register("saline_mud",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5f,0.5f)
                    .sound(SoundType.SLIME_BLOCK)
            ));

    public static final RegistryObject<Block> SALINE_FARMLAND = BLOCKS.register("saline_farmland",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.6f,0.6f)
                    .sound(SoundType.GRAVEL)
            ));

    public static final RegistryObject<Block> BLACK_SOIL = BLOCKS.register("black_soil",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BLACK)
                    .strength(0.5f,0.5f)
                    .sound(SoundType.GRAVEL)
            ));

    public static final RegistryObject<Block> BLACK_MUD = BLOCKS.register("black_mud",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BLACK)
                    .strength(0.5f,0.5f)
                    .sound(SoundType.SLIME_BLOCK)
            ));

    public static final RegistryObject<Block> BLACK_FARMLAND = BLOCKS.register("black_farmland",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BLACK)
                    .strength(0.6f,0.6f)
                    .sound(SoundType.GRAVEL)
            ));
    public static final RegistryObject<Block> NITER_BLOCK = BLOCKS.register("niter_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(1.2f,1.2f)
                    .sound(SoundType.NETHERRACK)
            ));

    public static final RegistryObject<Block> PAPER_MOLD = BLOCKS.register("paper_mold",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.5f,0.5f)
                    .sound(SoundType.BAMBOO_WOOD)
            ));

    public static final RegistryObject<Block> PULP_FILLED_PAPER_MOLD = BLOCKS.register("pulp_filled_paper_mold",
            () -> new Block(BlockBehaviour.Properties.copy(PAPER_MOLD.get())));

    public static final RegistryObject<Block> PAPER_FILLED_PAPER_MOLD = BLOCKS.register("paper_filled_paper_mold",
            () -> new Block(BlockBehaviour.Properties.copy(PAPER_MOLD.get())));

    public static final RegistryObject<Block> ANDESITE_FIRE_PIT = BLOCKS.register("andesite_fire_pit",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.5f)
                    .lightLevel(state -> 15) // 发光
                    .sound(SoundType.STONE)
            ));

    public static final RegistryObject<Block> BRASS_FIRE_PIT = BLOCKS.register("brass_fire_pit",
            () -> new Block(BlockBehaviour.Properties.copy(ANDESITE_FIRE_PIT.get())
                    .mapColor(MapColor.METAL)
            ));

    public static final RegistryObject<Block> MECHANICAL_FISHING_NET = BLOCKS.register("mechanical_fishing_net",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            ));

    // 新增示例：透明发光方块
    public static final RegistryObject<Block> LUMINOUS_MIRROR = BLOCKS.register("luminous_mirror",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(1.5f)
                    .sound(SoundType.GLASS)
//                    .lightLevel(state -> 11)
//                    .noOcclusion()
            ));

    // 新增示例：特殊冰块
//    public static final RegistryObject<Block> ANCIENT_ICE = BLOCKS.register("ancient_ice",
//            () -> new Block(BlockBehaviour.Properties.of()
//                    .mapColor(MapColor.COLOR_BLUE)
//                    .friction(0.98f)
//                    .speedFactor(1.2f)
//                    .jumpFactor(1.1f)
//                    .strength(0.5f)
//                    .sound(SoundType.GLASS)
//                    .pushReaction(PushReaction.NORMAL)
//            ));
    public static final RegistryObject<BlockEntityType<ElectricBurnerBlockEntity>>
            ELECTRIC_BURNER_ENTITY = BLOCK_ENTITY_TYPES.register("electric_burner",
            () -> BlockEntityType.Builder.of(ElectricBurnerBlockEntity::new, ELECTRIC_BURNER.get()).build(null)
    );
}