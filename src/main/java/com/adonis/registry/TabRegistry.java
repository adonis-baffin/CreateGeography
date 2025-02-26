package com.adonis.registry;

import com.adonis.fluid.GeographyFluids;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.adonis.CreateGeography;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Iterator;


public class TabRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateGeography.MODID);

    public static final RegistryObject<CreativeModeTab> creategeography_TAB = CREATIVE_TABS.register("creategeography_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.creategeography")).icon(
                () -> new ItemStack(ItemRegistry.COPPER_COIL.get())
            ).displayItems((params, output) ->
            {
                Iterator<RegistryObject<Item>> var2 = ItemRegistry.ITEMS.getEntries().iterator();
                while(var2.hasNext()) {
                    RegistryObject<Item> item = var2.next();
                    if(!(item.get() instanceof SequencedAssemblyItem)) {
                        output.accept(item.get());
                    }
                }
                output.accept(GeographyFluids.PLANT_OIL.getBucket().get());
            }).build());

}
