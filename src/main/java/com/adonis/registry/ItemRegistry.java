package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.item.*;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

import static com.adonis.CreateGeography.REGISTRATE;

public class ItemRegistry {

    // 保留原有的DeferredRegister用于不需要tooltip的物品
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreateGeography.MODID);
    public static LinkedHashSet<RegistryObject<Item>> CREATIVE_TAB_ITEMS = new LinkedHashSet<>();

    // 辅助方法
    public static RegistryObject<Item> registerWithTab(final String name, final Supplier<Item> supplier) {
        RegistryObject<Item> item = ITEMS.register(name, supplier);
        CREATIVE_TAB_ITEMS.add(item);
        return item;
    }

    public static Item.Properties basicItem() {
        return new Item.Properties();
    }

    public static Item.Properties foodItem(FoodProperties food) {
        return new Item.Properties().food(food);
    }

    // === 使用CreateRegistrate注册的重要物品（会有tooltip） ===

    // 工具与装备使用Registrate注册
    public static final ItemEntry<GeofragmentatorItem> GEOFRAGMENTATOR = REGISTRATE.item("geofragmentator", GeofragmentatorItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<TrekkingPoles> TREKKING_POLES = REGISTRATE.item("trekking_poles", TrekkingPoles::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<IndustrialShearsItem> INDUSTRIAL_SHEARS = REGISTRATE.item("industrial_shears", IndustrialShearsItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<BrineBottleItem> BRINE_BOTTLE = REGISTRATE.item("brine_bottle", BrineBottleItem::new)
            .register();

    // 基础材料
    public static final RegistryObject<Item> CRUSHED_STONE = registerWithTab("crushed_stone", () -> new Item(basicItem()));
    public static final RegistryObject<Item> CRUSHED_DEEP_SLATE = registerWithTab("crushed_deep_slate", () -> new Item(basicItem()));
    public static final RegistryObject<Item> SAND_DUST = registerWithTab("sand_dust", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ORTHACLASE = registerWithTab("orthoclase", () -> new Item(basicItem()));
    public static final RegistryObject<Item> RED_SAND_DUST = registerWithTab("red_sand_dust", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PLAGIOCLASE = registerWithTab("plagioclase", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ASH = registerWithTab("ash", () -> new Item(basicItem()));
    public static final RegistryObject<Item> QUARTZ_SAND = registerWithTab("quartz_sand", () -> new Item(basicItem()));
    public static final RegistryObject<Item> SULFUR_POWDER = registerWithTab("sulfur_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> MARBLE = registerWithTab("marble", () -> new Item(basicItem()));
    public static final RegistryObject<Item> MARBLE_POWDER = registerWithTab("marble_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> HORNBLENDE = registerWithTab("hornblende", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POLISHED_HORNBLENDE = registerWithTab("polished_hornblende", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PYROXENE = registerWithTab("pyroxene", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POLISHED_PYROXENE = registerWithTab("polished_pyroxene", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PYROXENE_POWDER = registerWithTab("pyroxene_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> NITER = registerWithTab("niter", () -> new Item(basicItem()));
    public static final RegistryObject<Item> NITER_POWDER = registerWithTab("niter_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> COAL_POWDER = registerWithTab("coal_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> CHARCOAL_POWDER = registerWithTab("charcoal_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> LAPIS_LAZULI_POWDER = registerWithTab("lapis_lazuli_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> BIOTITE = registerWithTab("biotite", () -> new Item(basicItem()));
    public static final RegistryObject<Item> SALT = registerWithTab("salt", () -> new Item(basicItem()));
    public static final RegistryObject<Item> WOOD_FIBER = registerWithTab("wood_fiber", () -> new Item(basicItem()));
    public static final RegistryObject<Item> DIRT_CLOD = registerWithTab("dirt_clod", () -> new Item(basicItem()));
    public static final RegistryObject<Item> CALCAREOUS_DEBRIS = registerWithTab("calcareous_debris", () -> new Item(basicItem()));
    public static final RegistryObject<Item> QUICK_LIME = registerWithTab("quick_lime", () -> new Item(basicItem()));
    public static final RegistryObject<Item> SLAKED_LIME = registerWithTab("slaked_lime", () -> new Item(basicItem()));

    // 冰相关物品
    public static final RegistryObject<Item> ICE_SHARDS = registerWithTab("ice_shards", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PACKED_ICE_SHARDS = registerWithTab("packed_ice_shards", () -> new Item(basicItem()));
    public static final RegistryObject<Item> BLUE_ICE_SHARDS = registerWithTab("blue_ice_shards", () -> new Item(basicItem()));

    // 矿石粉末
    public static final RegistryObject<Item> IRON_ORE_POWDER = registerWithTab("iron_ore_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> COPPER_ORE_POWDER = registerWithTab("copper_ore_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> GOLD_ORE_POWDER = registerWithTab("gold_ore_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ZINC_ORE_POWDER = registerWithTab("zinc_ore_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> OSMIUM_ORE_POWDER = registerWithTab("osmium_ore_powder", () -> new Item(basicItem()));

    // 不需要tooltip的方块物品
    public static final RegistryObject<Item> SALT_CRYSTAL = registerWithTab("salt_crystal",
            () -> new BlockItem(BlockRegistry.SALT_CRYSTAL.get(), basicItem()));
    public static final RegistryObject<Item> NITER_CRYSTAL = registerWithTab("niter_crystal",
            () -> new BlockItem(BlockRegistry.NITER_CRYSTAL.get(), basicItem()));
    public static final RegistryObject<Item> SALT_BLOCK = registerWithTab("salt_block",
            () -> new BlockItem(BlockRegistry.SALT_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> NITER_BLOCK = registerWithTab("niter_block",
            () -> new BlockItem(BlockRegistry.NITER_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_ICE = registerWithTab("cracked_ice",
            () -> new BlockItem(BlockRegistry.CRACKED_ICE.get(), basicItem()));

    public static final RegistryObject<Item> IRON_BEARING_BLUE_ICE = registerWithTab("iron_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.IRON_BEARING_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> COPPER_BEARING_BLUE_ICE = registerWithTab("copper_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.COPPER_BEARING_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> GOLD_BEARING_BLUE_ICE = registerWithTab("gold_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.GOLD_BEARING_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> ZINC_BEARING_BLUE_ICE = registerWithTab("zinc_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.ZINC_BEARING_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> OSMIUM_BEARING_BLUE_ICE = registerWithTab("osmium_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.OSMIUM_BEARING_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> COAL_BEARING_BLUE_ICE = registerWithTab("coal_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.COAL_BEARING_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> LAPIS_LAZULI_BEARING_BLUE_ICE = registerWithTab("lapis_lazuli_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.LAPIS_LAZULI_BEARING_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> REDSTONE_BEARING_BLUE_ICE = registerWithTab("redstone_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.REDSTONE_BEARING_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> SALT_BEARING_BLUE_ICE = registerWithTab("salt_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.SALT_BEARING_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> NITER_BEARING_BLUE_ICE = registerWithTab("niter_bearing_blue_ice",
            () -> new BlockItem(BlockRegistry.NITER_BEARING_BLUE_ICE.get(), basicItem()));
}