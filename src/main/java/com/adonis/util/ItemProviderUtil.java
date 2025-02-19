package com.adonis.util;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.adonis.CreateGeography;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class ItemProviderUtil {

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateGeography.MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public static BlockEntry<?> createBlockEntry(RegistryObject<Block> block) {
        return new BlockEntry<>(REGISTRATE, block);
    }

}
