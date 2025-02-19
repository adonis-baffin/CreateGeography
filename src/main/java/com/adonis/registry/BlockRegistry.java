package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.block.ElectricBurnerBlock;
import com.adonis.content.block.entity.ElectricBurnerBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreateGeography.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreateGeography.MODID);

    public static final RegistryObject<Block> ELECTRIC_BURNER = BLOCKS.register("electric_burner",
            () -> new ElectricBurnerBlock(Properties.copy(Blocks.STONE))
    );

    public static final RegistryObject<BlockEntityType<ElectricBurnerBlockEntity>>
            ELECTRIC_BURNER_ENTITY = BLOCK_ENTITY_TYPES.register("electric_burner",
            () -> Builder.of(ElectricBurnerBlockEntity::new, ELECTRIC_BURNER.get()).build(null)
    );


}
