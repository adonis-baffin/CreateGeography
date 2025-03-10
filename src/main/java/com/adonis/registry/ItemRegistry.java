package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.content.item.GeologicalHammerItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
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
    public static final RegistryObject<Item> PEBBLE = registerWithTab("pebble", () -> new Item(basicItem()));
    public static final RegistryObject<Item> CRUSHED_DEEP_SLATE = registerWithTab("crushed_deep_slate", () -> new Item(basicItem()));
    public static final RegistryObject<Item> DEEP_SLATE_PEBBLE = registerWithTab("deep_slate_pebble", () -> new Item(basicItem()));
    public static final RegistryObject<Item> SAND_DUST = registerWithTab("sand_dust", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ORTHACLASE = registerWithTab("orthoclase", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ORTHACLASE_PEBBLE = registerWithTab("orthoclase_pebble", () -> new Item(basicItem()));
    public static final RegistryObject<Item> RED_SAND_DUST = registerWithTab("red_sand_dust", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PLAGIOCLASE = registerWithTab("plagioclase", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PLAGIOCLASE_PEBBLE = registerWithTab("plagioclase_pebble", () -> new Item(basicItem()));
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
    public static final RegistryObject<Item> ICE_SHARDS = registerWithTab("ice_shards", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PACKED_ICE_SHARDS = registerWithTab("packed_ice_shards", () -> new Item(basicItem()));
    public static final RegistryObject<Item> BLUE_ICE_SHARDS = registerWithTab("blue_ice_shards", () -> new Item(basicItem()));
    public static final RegistryObject<Item> SALT = registerWithTab("salt", () -> new Item(basicItem()));
    public static final RegistryObject<Item> WOOD_FIBER = registerWithTab("wood_fiber", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PYROXENE_GLASS_PANE = registerWithTab("pyroxene_glass_pane", () -> new Item(basicItem()));
    public static final RegistryObject<Item> GEOLOGICAL_HAMMER = ITEMS.register("geological_hammer",
            () -> new GeologicalHammerItem(basicItem()));
    public static final RegistryObject<Item> WALKING_STAFF = registerWithTab("walking_staff", () -> new Item(basicItem()));
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
    public static final RegistryObject<Item> LUMINOUS_MIRROR = registerWithTab("luminous_mirror", () -> new BlockItem(BlockRegistry.LUMINOUS_MIRROR.get(), basicItem()));
    public static final RegistryObject<Item> SOLAR_HEATER = registerWithTab("solar_heater", () -> new BlockItem(BlockRegistry.SOLAR_HEATER.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_ICE = registerWithTab("cracked_ice", () -> new BlockItem(BlockRegistry.CRACKED_ICE.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_PACKED_ICE = registerWithTab("cracked_packed_ice", () -> new BlockItem(BlockRegistry.CRACKED_PACKED_ICE.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_BLUE_ICE = registerWithTab("cracked_blue_ice", () -> new BlockItem(BlockRegistry.CRACKED_BLUE_ICE.get(), basicItem()));
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
//    public static final RegistryObject<Item> FROZEN_DIRT = registerWithTab("frozen_dirt", () -> new BlockItem(BlockRegistry.FROZEN_DIRT.get(), basicItem()));
//    public static final RegistryObject<Item> FROZEN_MUD = registerWithTab("frozen_mud", () -> new BlockItem(BlockRegistry.FROZEN_MUD.get(), basicItem()));
    public static final RegistryObject<Item> SALT_BLOCK = registerWithTab("salt_block", () -> new BlockItem(BlockRegistry.SALT_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> SALINE_SOIL = registerWithTab("saline_soil", () -> new BlockItem(BlockRegistry.SALINE_DIRT.get(), basicItem()));
    public static final RegistryObject<Item> SALINE_MUD = registerWithTab("saline_mud", () -> new BlockItem(BlockRegistry.SALINE_MUD.get(), basicItem()));
    public static final RegistryObject<Item> SALINE_FARMLAND = registerWithTab("saline_farmland", () -> new BlockItem(BlockRegistry.SALINE_FARMLAND.get(), basicItem()));
    public static final RegistryObject<Item> BLACK_SOIL = registerWithTab("black_soil", () -> new BlockItem(BlockRegistry.BLACK_DIRT.get(), basicItem()));
    public static final RegistryObject<Item> BLACK_MUD = registerWithTab("black_mud", () -> new BlockItem(BlockRegistry.BLACK_MUD.get(), basicItem()));
    public static final RegistryObject<Item> BLACK_FARMLAND_ITEM = registerWithTab("black_farmland",
            () -> new BlockItem(BlockRegistry.BLACK_FARMLAND.get(), basicItem()));
    public static final RegistryObject<Item> NITER_BLOCK = registerWithTab("niter_block", () -> new BlockItem(BlockRegistry.NITER_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> PAPER_MOLD = registerWithTab("paper_mold", () -> new BlockItem(BlockRegistry.PAPER_MOLD.get(), basicItem()));
    public static final RegistryObject<Item> PULP_FILLED_PAPER_MOLD = registerWithTab("pulp_filled_paper_mold", () -> new BlockItem(BlockRegistry.PULP_FILLED_PAPER_MOLD.get(), basicItem()));
    public static final RegistryObject<Item> SALT_WATER_FILLED_PAPER_MOLD = registerWithTab("salt_water_filled_paper_mold", () -> new BlockItem(BlockRegistry.SALT_WATER_FILLED_PAPER_MOLD.get(), basicItem()));
    public static final RegistryObject<Item> MUD_FILLED_PAPER_MOLD = registerWithTab("mud_filled_paper_mold", () -> new BlockItem(BlockRegistry.MUD_FILLED_PAPER_MOLD.get(), basicItem()));
    public static final RegistryObject<Item> SAND_SLURRY_FILLED_PAPER_MOLD = registerWithTab("sand_slurry_filled_paper_mold", () -> new BlockItem(BlockRegistry.SAND_SLURRY_FILLED_PAPER_MOLD.get(), basicItem()));
    public static final RegistryObject<Item> TRUE_ICE = registerWithTab("true_ice", () -> new BlockItem(BlockRegistry.TRUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> ANDESITE_FIRE_PIT = registerWithTab("andesite_fire_pit", () -> new BlockItem(BlockRegistry.ANDESITE_FIRE_PIT.get(), basicItem()));
    public static final RegistryObject<Item> BRASS_FIRE_PIT = registerWithTab("brass_fire_pit", () -> new BlockItem(BlockRegistry.BRASS_FIRE_PIT.get(), basicItem()));
    public static final RegistryObject<Item> MECHANICAL_FISHING_NET = registerWithTab("mechanical_fishing_net", () -> new BlockItem(BlockRegistry.MECHANICAL_FISHING_NET.get(), basicItem()));
//    public static final RegistryObject<Item> POWERED_RAMMER = registerWithTab("powered_rammer", () -> new BlockItem(BlockRegistry.POWERED_RAMMER.get(), basicItem()));
//    public static final RegistryObject<Item> POWERED_COMPRESSOR = registerWithTab("powered_compressor", () -> new BlockItem(BlockRegistry.POWERED_COMPRESSOR.get(), basicItem()));
//    public static final RegistryObject<Item> SPRINKLER = registerWithTab("sprinkler", () -> new BlockItem(BlockRegistry.SPRINKLER.get(), basicItem()));
//    public static final RegistryObject<Item> ALLOY_FORGING_FURNACE = registerWithTab("alloy_forging_furnace", () -> new BlockItem(BlockRegistry.ALLOY_FORGING_FURNACE.get(), basicItem()));
//    public static final RegistryObject<Item> CRUCIBLE = registerWithTab("crucible", () -> new BlockItem(BlockRegistry.CRUCIBLE.get(), basicItem()));
//    public static final RegistryObject<Item> FLEXIBLE_HOSE = registerWithTab("flexible_hose", () -> new BlockItem(BlockRegistry.FLEXIBLE_HOSE.get(), basicItem()));
//    public static final RegistryObject<Item> LIQUID_PUMP = registerWithTab("liquid_pump", () -> new BlockItem(BlockRegistry.LIQUID_PUMP.get(), basicItem()));
//    public static final RegistryObject<Item> INTERFACE_TANK = registerWithTab("interface_tank", () -> new BlockItem(BlockRegistry.INTERFACE_TANK.get(), basicItem()));
//    public static final RegistryObject<Item> CONDENSATION_TANK = registerWithTab("condensation_tank", () -> new BlockItem(BlockRegistry.CONDENSATION_TANK.get(), basicItem()));
//    public static final RegistryObject<Item> VOLTAIC_BATTERY_BOX = registerWithTab("voltaic_battery_box", () -> new BlockItem(BlockRegistry.VOLTAIC_BATTERY_BOX.get(), basicItem()));
//    public static final RegistryObject<Item> PHOTOVOLTAIC_BATTERY_BOX = registerWithTab("photovoltaic_battery_box", () -> new BlockItem(BlockRegistry.PHOTOVOLTAIC_BATTERY_BOX.get(), basicItem()));
}