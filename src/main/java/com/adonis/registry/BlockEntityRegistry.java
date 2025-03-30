package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.block.DirtClodFilledWoodenFrameBlockEntity;
import com.adonis.content.block.SaltFilledWoodenFrameBlockEntity;
import com.adonis.content.block.SandDustFilledWoodenFrameBlockEntity;
import com.adonis.content.block.entity.ElectricBurnerBlockEntity;
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
    public static final RegistryObject<BlockEntityType<SaltFilledWoodenFrameBlockEntity>> SALT_FILLED_WOODEN_FRAME = BLOCK_ENTITY_TYPES.register("salt_filled_wooden_frame",
            () -> BlockEntityType.Builder.of(SaltFilledWoodenFrameBlockEntity::new, BlockRegistry.SALT_FILLED_WOODEN_FRAME.get()).build(null));
            
    public static final RegistryObject<BlockEntityType<DirtClodFilledWoodenFrameBlockEntity>> DIRT_CLOD_FILLED_WOODEN_FRAME = BLOCK_ENTITY_TYPES.register("dirt_clod_filled_wooden_frame",
            () -> BlockEntityType.Builder.of(DirtClodFilledWoodenFrameBlockEntity::new, BlockRegistry.DIRT_CLOD_FILLED_WOODEN_FRAME.get()).build(null));
            
    public static final RegistryObject<BlockEntityType<SandDustFilledWoodenFrameBlockEntity>> SAND_DUST_FILLED_WOODEN_FRAME = BLOCK_ENTITY_TYPES.register("sand_dust_filled_wooden_frame",
            () -> BlockEntityType.Builder.of(SandDustFilledWoodenFrameBlockEntity::new, BlockRegistry.SAND_DUST_FILLED_WOODEN_FRAME.get()).build(null));
}