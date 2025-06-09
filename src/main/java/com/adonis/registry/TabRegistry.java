package com.adonis.registry;

import com.adonis.fluid.GeographyFluids;
import com.adonis.CreateGeography;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TabRegistry {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateGeography.MODID);

    public static final RegistryObject<CreativeModeTab> creategeography_TAB = CREATIVE_TABS.register("creategeography_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.creategeography")).icon(
                    () -> new ItemStack(ItemRegistry.GEOFRAGMENTATOR.get())
            ).displayItems((params, output) -> {
                // 基础材料
                output.accept(ItemRegistry.HORNBLENDE.get());              // 角闪石
                output.accept(ItemRegistry.POLISHED_HORNBLENDE.get());     // 磨制角闪石
                output.accept(ItemRegistry.CRUSHED_STONE.get());           // 砾石
                output.accept(ItemRegistry.CRUSHED_DEEP_SLATE.get());      // 深板岩砾石
                output.accept(ItemRegistry.ORTHACLASE.get());              // 正长石
                output.accept(ItemRegistry.PLAGIOCLASE.get());             // 斜长石
                output.accept(ItemRegistry.SAND_DUST.get());               // 沙尘
                output.accept(ItemRegistry.RED_SAND_DUST.get());           // 红沙尘
                output.accept(ItemRegistry.ASH.get());                     // 灰烬
                output.accept(ItemRegistry.QUARTZ_SAND.get());             // 石英砂
                output.accept(ItemRegistry.NITER.get());                   // 硝石
                output.accept(ItemRegistry.SULFUR_POWDER.get());           // 硫磺粉
                output.accept(ItemRegistry.COAL_POWDER.get());             // 煤粉
                output.accept(ItemRegistry.CHARCOAL_POWDER.get());         // 木炭粉
                output.accept(ItemRegistry.NITER_POWDER.get());            // 硝石粉
                output.accept(ItemRegistry.SALT.get());                    // 盐
                output.accept(ItemRegistry.DIRT_CLOD.get());               // 土坷

                // 方块物品
                output.accept(ItemRegistry.FROZEN_SOIL.get());             // 冻土
                output.accept(ItemRegistry.SALT_CRYSTAL.get());            // 盐晶
                output.accept(ItemRegistry.SALINE_DIRT.get());             // 盐碱土
                output.accept(ItemRegistry.SALINE_MUD.get());              // 盐碱泥巴
                output.accept(ItemRegistry.SALINE_FARMLAND.get());         // 盐碱耕地
                output.accept(ItemRegistry.SALT_BLOCK.get());              // 盐块

                // 机器和特殊物品
//                output.accept(ItemRegistry.INDUSTRIAL_FURNACE_ITEM.get()); // 工业熔炉
//                output.accept(ItemRegistry.INDUSTRIAL_COMPOSTER.get());    // 工业堆肥桶
//                output.accept(ItemRegistry.INDUSTRIAL_ANVIL_ITEM.get());   // 工业铁砧
                output.accept(ItemRegistry.TREKKING_POLES.get());          // 登山杖
                output.accept(ItemRegistry.GEOFRAGMENTATOR.get());         // 地质破碎锤

                // 流体桶
                output.accept(GeographyFluids.BRINE.getBucket().get());    // 盐水桶
            }).build());
}