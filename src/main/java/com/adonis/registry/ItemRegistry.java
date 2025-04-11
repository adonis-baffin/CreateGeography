package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.block.WoodenFrameItem;
import com.adonis.content.item.*;
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

public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreateGeography.MODID);
    public static LinkedHashSet<RegistryObject<Item>> CREATIVE_TAB_ITEMS = new LinkedHashSet<>();

    public static RegistryObject<Item> registerWithTab(final String name, final Supplier<Item> supplier) {
        RegistryObject<Item> item = ITEMS.register(name, supplier);
        CREATIVE_TAB_ITEMS.add(item);
        return item;
    }

    // Helper methods
    public static Item.Properties basicItem() {
        return new Item.Properties();
    }

    public static Item.Properties foodItem(FoodProperties food) {
        return new Item.Properties().food(food);
    }

    // Normal Items
    public static final RegistryObject<Item> CRUSHED_STONE = registerWithTab("crushed_stone", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PEBBLE = registerWithTab("pebble",
            () -> new ThrowablePebbleItem(basicItem()));    public static final RegistryObject<Item> CRUSHED_DEEP_SLATE = registerWithTab("crushed_deep_slate", () -> new Item(basicItem()));
    public static final RegistryObject<Item> DEEP_SLATE_PEBBLE = registerWithTab("deep_slate_pebble",
            () -> new ThrowablePebbleItem(basicItem()));    public static final RegistryObject<Item> SAND_DUST = registerWithTab("sand_dust", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ORTHACLASE = registerWithTab("orthoclase", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ORTHACLASE_PEBBLE = registerWithTab("orthoclase_pebble",
            () -> new ThrowablePebbleItem(basicItem()));    public static final RegistryObject<Item> RED_SAND_DUST = registerWithTab("red_sand_dust", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PLAGIOCLASE = registerWithTab("plagioclase", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PLAGIOCLASE_PEBBLE = registerWithTab("plagioclase_pebble",
            () -> new ThrowablePebbleItem(basicItem()));    public static final RegistryObject<Item> ASH = registerWithTab("ash", () -> new Item(basicItem()));
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
    public static final RegistryObject<Item> ICE_SHARDS = registerWithTab("ice_shards", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PACKED_ICE_SHARDS = registerWithTab("packed_ice_shards", () -> new Item(basicItem()));
    public static final RegistryObject<Item> BLUE_ICE_SHARDS = registerWithTab("blue_ice_shards", () -> new Item(basicItem()));
    public static final RegistryObject<Item> SALT = registerWithTab("salt", () -> new Item(basicItem()));
    public static final RegistryObject<Item> WOOD_FIBER = registerWithTab("wood_fiber", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PYROXENE_GLASS_PANE = registerWithTab("pyroxene_glass_pane", () -> new Item(basicItem()));
    public static final RegistryObject<Item> GEOFRAGMENTATOR = ITEMS.register("geofragmentator",
            () -> new GeofragmentatorItem(basicItem()));
    public static final RegistryObject<Item> TREKKING_POLES = ITEMS.register("trekking_poles",
            () -> new TrekkingPoles(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ZINC_PLATE = registerWithTab("zinc_plate", () -> new Item(basicItem()));
    public static final RegistryObject<Item> VOLTAIC_PILE = registerWithTab("voltaic_pile", () -> new Item(basicItem()));
    public static final RegistryObject<Item> SILICON_PLATE = registerWithTab("silicon_plate", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PYROXENE_GLASS_BLADE = registerWithTab("pyroxene_glass_blade", () -> new Item(basicItem()));
    public static final RegistryObject<Item> CATAPULTIC_GRAPPLING_HOOK = registerWithTab("catapultic_grappling_hook", () -> new Item(basicItem()));
    public static final RegistryObject<Item> DETACHABLE_BRASS_HILT = registerWithTab("detachable_brass_hilt", () -> new Item(basicItem()));
    public static final RegistryObject<Item> GREASE = registerWithTab("grease", () -> new Item(basicItem()));
    public static final RegistryObject<Item> WINDPROOF_COAT = registerWithTab("windproof_coat", () -> new Item(basicItem()));
    public static final RegistryObject<Item> COPPER_DIVING_LEGGINGS = registerWithTab("copper_diving_leggings", () -> new Item(basicItem()));
    public static final RegistryObject<Item> NETHERITE_DIVING_LEGGINGS = registerWithTab("netherite_diving_leggings", () -> new Item(basicItem()));
    public static final RegistryObject<Item> MARBLE_HORNED_HELMET = registerWithTab("marble_horned_helmet", () -> new Item(basicItem()));
    public static final RegistryObject<Item> COPPER_COIL = registerWithTab("copper_coil", () -> new Item(basicItem()));
    public static final RegistryObject<Item> NICKEL_COIL = registerWithTab("nickel_coil", () -> new Item(basicItem()));
    public static final RegistryObject<Item> BATTER = registerWithTab("batter", () -> new Item(basicItem()));
    public static final RegistryObject<Item> IRON_ORE_POWDER = registerWithTab("iron_ore_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> COPPER_ORE_POWDER = registerWithTab("copper_ore_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> GOLD_ORE_POWDER = registerWithTab("gold_ore_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ZINC_ORE_POWDER = registerWithTab("zinc_ore_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> OSMIUM_ORE_POWDER = registerWithTab("osmium_ore_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> DIRT_CLOD = registerWithTab("dirt_clods", () -> new Item(basicItem()));

    // Foods
    public static final RegistryObject<Item> PANCAKE = registerWithTab("pancake", () -> new Item(foodItem(new FoodProperties.Builder().nutrition(12).saturationMod(19.2f).build())));
    public static final RegistryObject<Item> JAM_PANCAKE = registerWithTab("jam_pancake", () -> new Item(foodItem(new FoodProperties.Builder().nutrition(14).saturationMod(21f).build())));
    public static final RegistryObject<Item> HONEY_PANCAKE = registerWithTab("honey_pancake", () -> new Item(foodItem(new FoodProperties.Builder().nutrition(14).saturationMod(21f).build())));
    public static final RegistryObject<Item> CHOCOLATE_PANCAKE = registerWithTab("chocolate_pancake", () -> new Item(foodItem(new FoodProperties.Builder().nutrition(14).saturationMod(21f).build())));

    // Block Items
    public static final RegistryObject<Item> SAND_PILE = registerWithTab("sand_pile", () -> new BlockItem(BlockRegistry.SAND_PILE.get(), basicItem()));
    public static final RegistryObject<Item> SNOW_PILE = registerWithTab("snow_pile", () -> new BlockItem(BlockRegistry.SNOW_PILE.get(), basicItem()));
    public static final RegistryObject<Item> ASH_PILE = registerWithTab("ash_pile", () -> new BlockItem(BlockRegistry.ASH_PILE.get(), basicItem()));
    public static final RegistryObject<Item> PEBBLE_BLOCK = registerWithTab("pebble_block", () -> new BlockItem(BlockRegistry.PEBBLE_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> PARENT_MATERIAL = registerWithTab("parent_material", () -> new BlockItem(BlockRegistry.PARENT_MATERIAL.get(), basicItem()));
    public static final RegistryObject<Item> MARBLE_BLOCK = registerWithTab("marble_block", () -> new BlockItem(BlockRegistry.MARBLE_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> PEBBLE_PATH = registerWithTab("pebble_path", () -> new BlockItem(BlockRegistry.PEBBLE_PATH.get(), basicItem()));
    public static final RegistryObject<Item> QUARTZITE_PATH = registerWithTab("quartzite_path", () -> new BlockItem(BlockRegistry.QUARTZITE_PATH.get(), basicItem()));
    public static final RegistryObject<Item> MARBLE_PATH = registerWithTab("marble_path", () -> new BlockItem(BlockRegistry.MARBLE_PATH.get(), basicItem()));
    public static final RegistryObject<Item> PYROXENE_GLASS = registerWithTab("pyroxene_glass", () -> new BlockItem(BlockRegistry.PYROXENE_GLASS.get(), basicItem()));
//    public static final RegistryObject<Item> PYROXENE_MIRROR = registerWithTab("pyroxene_mirror", () -> new BlockItem(BlockRegistry.PYROXENE_MIRROR.get(), basicItem()));
//    public static final RegistryObject<Item> PYROXENE_HEATER = registerWithTab("pyroxene_heater", () -> new BlockItem(BlockRegistry.PYROXENE_HEATER.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_ICE = registerWithTab("cracked_ice", () -> new BlockItem(BlockRegistry.CRACKED_ICE.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_PACKED_ICE = registerWithTab("cracked_packed_ice", () -> new BlockItem(BlockRegistry.CRACKED_PACKED_ICE.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_BLUE_ICE = registerWithTab("cracked_blue_ice", () -> new BlockItem(BlockRegistry.CRACKED_BLUE_ICE.get(), basicItem()));
    // 新增轮次2破裂方块物品
    public static final RegistryObject<Item> CRACKED_BASALT = registerWithTab("cracked_basalt", () -> new BlockItem(BlockRegistry.CRACKED_BASALT.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_BLACKSTONE = registerWithTab("cracked_blackstone", () -> new BlockItem(BlockRegistry.CRACKED_BLACKSTONE.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_ANDESITE = registerWithTab("cracked_andesite", () -> new BlockItem(BlockRegistry.CRACKED_ANDESITE.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_GRANITE = registerWithTab("cracked_granite", () -> new BlockItem(BlockRegistry.CRACKED_GRANITE.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_DIORITE = registerWithTab("cracked_diorite", () -> new BlockItem(BlockRegistry.CRACKED_DIORITE.get(), basicItem()));
    // 含矿蓝冰物品
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
    public static final RegistryObject<Item> SALT_CRYSTAL = registerWithTab("salt_crystal", () -> new BlockItem(BlockRegistry.SALT_CRYSTAL.get(), basicItem()));
    public static final RegistryObject<Item> SALT_BLOCK = registerWithTab("salt_block", () -> new BlockItem(BlockRegistry.SALT_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> SALINE_SOIL = registerWithTab("saline_soil", () -> new BlockItem(BlockRegistry.SALINE_DIRT.get(), basicItem()));
    public static final RegistryObject<Item> SALINE_MUD = registerWithTab("saline_mud", () -> new BlockItem(BlockRegistry.SALINE_MUD.get(), basicItem()));
    public static final RegistryObject<Item> SALINE_FARMLAND = registerWithTab("saline_farmland", () -> new BlockItem(BlockRegistry.SALINE_FARMLAND.get(), basicItem()));
    public static final RegistryObject<Item> BLACK_SOIL = registerWithTab("black_soil", () -> new BlockItem(BlockRegistry.BLACK_DIRT.get(), basicItem()));
    public static final RegistryObject<Item> BLACK_MUD = registerWithTab("black_mud", () -> new BlockItem(BlockRegistry.BLACK_MUD.get(), basicItem()));
    public static final RegistryObject<Item> BLACK_FARMLAND_ITEM = registerWithTab("black_farmland",
            () -> new BlockItem(BlockRegistry.BLACK_FARMLAND.get(), basicItem()));
    public static final RegistryObject<Item> NITER_BLOCK = registerWithTab("niter_block", () -> new BlockItem(BlockRegistry.NITER_BLOCK.get(), basicItem()));

    // ... 现有代码 ...

    // ItemRegistry.java (修正部分)
    // ... 现有代码 ...

    // 修复爆炸箭物品注册
    public static final RegistryObject<Item> EXPLOSIVE_ARROW = ITEMS.register(
            "explosive_arrow",
            () -> new com.adonis.content.item.ExplosiveArrowItem(new Item.Properties().stacksTo(64))
    );
    // ... 现有代码 ...

    // 注册末影水晶爆炸箭物品
    public static final RegistryObject<Item> ENDER_CRYSTAL_ARROW = ITEMS.register(
            "ender_crystal_arrow",
            () -> new EnderCrystalArrowItem(new Item.Properties().stacksTo(64)) // 由于威力巨大，减少堆叠数量
    );

    // ... 现有代码 ...

    // 注册闪电箭物品
    public static final RegistryObject<Item> LIGHTNING_ARROW = ITEMS.register(
            "lightning_arrow",
            () -> new LightningArrowItem(new Item.Properties().stacksTo(64)) // 由于威力巨大，减少堆叠数量
    );

// ... 现有代码 ...

// ... 现有代码 ...

    // ... 现有代码 ...

    // ... 现有代码 ...
    // 普通木框
    public static final RegistryObject<Item> WOODEN_FRAME = registerWithTab("wooden_frame",
            () -> new WoodenFrameItem(BlockRegistry.WOODEN_FRAME.get(), basicItem()));

    // 装有水的木框
    public static final RegistryObject<Item> WATER_FILLED_WOODEN_FRAME = registerWithTab("water_filled_wooden_frame",
            () -> new BlockItem(BlockRegistry.WATER_FILLED_WOODEN_FRAME.get(), basicItem()));

    // 装有盐水的木框
    public static final RegistryObject<Item> BRINE_FILLED_WOODEN_FRAME = registerWithTab("brine_filled_wooden_frame",
            () -> new BlockItem(BlockRegistry.BRINE_FILLED_WOODEN_FRAME.get(), basicItem()));

    // 装有盐的木框
    public static final RegistryObject<Item> SALT_FILLED_WOODEN_FRAME = registerWithTab("salt_filled_wooden_frame",
            () -> new BlockItem(BlockRegistry.SALT_FILLED_WOODEN_FRAME.get(), basicItem()));
    // 泥浆木框
    public static final RegistryObject<Item> MUD_FILLED_WOODEN_FRAME = registerWithTab("mud_filled_wooden_frame",
            () -> new BlockItem(BlockRegistry.MUD_FILLED_WOODEN_FRAME.get(), basicItem()));

    // 沙浆木框
    public static final RegistryObject<Item> SAND_SLURRY_FILLED_WOODEN_FRAME = registerWithTab("sand_slurry_filled_wooden_frame",
            () -> new BlockItem(BlockRegistry.SAND_SLURRY_FILLED_WOODEN_FRAME.get(), basicItem()));

    // 沙尘和土坷木框
    public static final RegistryObject<Item> SAND_DUST_FILLED_WOODEN_FRAME = registerWithTab("sand_dust_filled_wooden_frame",
            () -> new BlockItem(BlockRegistry.SAND_DUST_FILLED_WOODEN_FRAME.get(), basicItem()));

    public static final RegistryObject<Item> DIRT_CLODS_FILLED_WOODEN_FRAME = registerWithTab("dirt_clods_filled_wooden_frame",
            () -> new BlockItem(BlockRegistry.DIRT_CLOD_FILLED_WOODEN_FRAME.get(), basicItem()));
    public static final RegistryObject<Item> TRUE_ICE = registerWithTab("true_ice", () -> new BlockItem(BlockRegistry.TRUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> ANDESITE_FIRE_PIT = registerWithTab("andesite_fire_pit", () -> new BlockItem(BlockRegistry.ANDESITE_FIRE_PIT.get(), basicItem()));
    public static final RegistryObject<Item> BRASS_FIRE_PIT = registerWithTab("brass_fire_pit", () -> new BlockItem(BlockRegistry.BRASS_FIRE_PIT.get(), basicItem()));
    public static final RegistryObject<Item> MECHANICAL_FISHING_NET = registerWithTab("mechanical_fishing_net", () -> new BlockItem(BlockRegistry.MECHANICAL_FISHING_NET.get(), basicItem()));

    // 修改后的物品注册代码
    public static final RegistryObject<Item> WOODEN_SICKLE = registerWithTab("wooden_sickle",
            () -> new SickleItem(Tiers.WOOD, 8.0F - 1.0F, new Item.Properties().stacksTo(1))); // 与铁斧相同的伤害

    public static final RegistryObject<Item> STONE_SICKLE = registerWithTab("stone_sickle",
            () -> new SickleItem(Tiers.STONE, 8.0F - 1.0F, new Item.Properties().stacksTo(1))); // 与铁斧相同的伤害

    public static final RegistryObject<Item> COPPER_SICKLE = registerWithTab("copper_sickle",
            () -> new SickleItem(Tiers.IRON, 8.0F - 1.0F, new Item.Properties().stacksTo(1))); // 与铁斧相同的伤害

    public static final RegistryObject<Item> IRON_SICKLE = registerWithTab("iron_sickle",
            () -> new SickleItem(Tiers.IRON, 9.0F - 1.0F, new Item.Properties().stacksTo(1))); // 与铁斧相同的伤害

    public static final RegistryObject<Item> GOLDEN_SICKLE = registerWithTab("golden_sickle",
            () -> new SickleItem(Tiers.GOLD, 7.0F - 1.0F, new Item.Properties().stacksTo(1))); // 与金斧相同的伤害

    public static final RegistryObject<Item> DIAMOND_SICKLE = registerWithTab("diamond_sickle",
            () -> new SickleItem(Tiers.DIAMOND, 9.0F - 1.0F, new Item.Properties().stacksTo(1))); // 与钻石斧相同的伤害

    public static final RegistryObject<Item> NETHERITE_SICKLE = registerWithTab("netherite_sickle",
            () -> new SickleItem(Tiers.NETHERITE, 10.0F - 1.0F, new Item.Properties().stacksTo(1).fireResistant())); // 与下界合金斧相同的伤害

    public static final RegistryObject<Item> INDUSTRIAL_SHEARS = registerWithTab("industrial_shears",
            () -> new IndustrialShearsItem(new Item.Properties().stacksTo(1)));
    // 工业堆肥桶
    public static final RegistryObject<Item> INDUSTRIAL_COMPOSTER = registerWithTab("industrial_composter",
            () -> new BlockItem(BlockRegistry.INDUSTRIAL_COMPOSTER.get(), basicItem()));
    // 工业铁砧物品
    public static final RegistryObject<Item> INDUSTRIAL_ANVIL_ITEM = ITEMS.register("industrial_anvil",
            () -> new BlockItem(BlockRegistry.INDUSTRIAL_ANVIL.get(), new Item.Properties()));

    // 工业熔炉物品
    public static final RegistryObject<Item> INDUSTRIAL_FURNACE_ITEM = ITEMS.register("industrial_furnace",
            () -> new BlockItem(BlockRegistry.INDUSTRIAL_FURNACE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ELECTRIC_BURNER_ITEM = ITEMS.register("electric_burner",
            () -> new BlockItem(BlockRegistry.ELECTRIC_BURNER.get(), new Item.Properties())
    );
}