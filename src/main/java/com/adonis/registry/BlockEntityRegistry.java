package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.block.*;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreateGeography.MODID);



    public static final RegistryObject<BlockEntityType<IndustrialFurnaceBlockEntity>> INDUSTRIAL_FURNACE =
            BLOCK_ENTITY_TYPES.register("industrial_furnace",
                    () -> BlockEntityType.Builder.of(IndustrialFurnaceBlockEntity::new, BlockRegistry.INDUSTRIAL_FURNACE.get())
                            .build(null));

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