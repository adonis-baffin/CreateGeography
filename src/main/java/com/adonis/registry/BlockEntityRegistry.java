package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.block.*;
import com.adonis.content.block.entity.ElectricBurnerBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.adonis.registry.BlockRegistry.ELECTRIC_BURNER;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreateGeography.MODID);

    public static final RegistryObject<BlockEntityType<ElectricBurnerBlockEntity>>
            ELECTRIC_BURNER_ENTITY = BLOCK_ENTITY_TYPES.register("electric_burner",
            () -> BlockEntityType.Builder.of(ElectricBurnerBlockEntity::new, ELECTRIC_BURNER.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<IndustrialFurnaceBlockEntity>> INDUSTRIAL_FURNACE =
            BLOCK_ENTITY_TYPES.register("industrial_furnace",
                    () -> BlockEntityType.Builder.of(IndustrialFurnaceBlockEntity::new, BlockRegistry.INDUSTRIAL_FURNACE.get())
                            .build(null));


    public static final BlockEntityEntry<PyroxeneMirrorBlockEntity> PYROXENE_MIRROR = CreateGeography.REGISTRATE
            .blockEntity("pyroxene_mirror", PyroxeneMirrorBlockEntity::new)
            .instance(() -> ShaftInstance::new)  // 绑定 ShaftInstance，支持传动杆
            .validBlocks(BlockRegistry.PYROXENE_MIRROR)  // 关联方块
            .renderer(() -> PyroxeneMirrorRenderer::new)  // 绑定渲染器
            .register();

    // Pyroxene Heater 注册
    public static final BlockEntityEntry<PyroxeneHeaterBlockEntity> PYROXENE_HEATER =
            CreateGeography.REGISTRATE
                    .blockEntity("pyroxene_heater", PyroxeneHeaterBlockEntity::new)
                    .validBlocks(BlockRegistry.PYROXENE_HEATER)
                    .register();

    // 静态方法创建 BlockEntityType（可选，当前未使用）
    private static BlockEntityType<PyroxeneMirrorBlockEntity> createPyroxeneMirrorType() {
        return BlockEntityType.Builder.of(
                (pos, state) -> new PyroxeneMirrorBlockEntity(PYROXENE_MIRROR.get(), pos, state),
                BlockRegistry.PYROXENE_MIRROR.get()
        ).build(null);
    }

    public static final RegistryObject<BlockEntityType<SaltFilledWoodenFrameBlockEntity>> SALT_FILLED_WOODEN_FRAME =
            BLOCK_ENTITY_TYPES.register("salt_filled_wooden_frame",
                    () -> BlockEntityType.Builder.of(SaltFilledWoodenFrameBlockEntity::new, BlockRegistry.SALT_FILLED_WOODEN_FRAME.get()).build(null));

    public static final RegistryObject<BlockEntityType<DirtClodFilledWoodenFrameBlockEntity>> DIRT_CLOD_FILLED_WOODEN_FRAME =
            BLOCK_ENTITY_TYPES.register("dirt_clod_filled_wooden_frame",
                    () -> BlockEntityType.Builder.of(DirtClodFilledWoodenFrameBlockEntity::new, BlockRegistry.DIRT_CLOD_FILLED_WOODEN_FRAME.get()).build(null));

    public static final RegistryObject<BlockEntityType<SandDustFilledWoodenFrameBlockEntity>> SAND_DUST_FILLED_WOODEN_FRAME =
            BLOCK_ENTITY_TYPES.register("sand_dust_filled_wooden_frame",
                    () -> BlockEntityType.Builder.of(SandDustFilledWoodenFrameBlockEntity::new, BlockRegistry.SAND_DUST_FILLED_WOODEN_FRAME.get()).build(null));
}