package com.adonis.registry;

import com.adonis.CreateGeography;
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
    public static final RegistryObject<Item> POWDERED_SAND = registerWithTab("powdered_sand", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ORTHACLASE = registerWithTab("orthoclase", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ORTHACLASE_PEBBLE = registerWithTab("orthoclase_pebble", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWEDERED_RED_SAND = registerWithTab("powdered_red_sand", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PLAGIOCLASE = registerWithTab("plagioclase", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PLAGIOCLASE_PEBBLE = registerWithTab("plagioclase_pebble", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ASH = registerWithTab("ash", () -> new Item(basicItem()));
    public static final RegistryObject<Item> COPPER_COIL = registerWithTab("copper_coil", () -> new Item(basicItem()));
    public static final RegistryObject<Item> NICKEL_COIL = registerWithTab("nickel_coil", () -> new Item(basicItem()));
    public static final RegistryObject<Item> QUARTZ_SAND = registerWithTab("quartz_sand", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_SULFUR = registerWithTab("powdered_sulfur", () -> new Item(basicItem()));
    public static final RegistryObject<Item> MARBLE = registerWithTab("marble", () -> new Item(basicItem()));
    public static final RegistryObject<Item> MARBLE_POWDER = registerWithTab("marble_powder", () -> new Item(basicItem()));
    public static final RegistryObject<Item> HORNBLENDE = registerWithTab("hornblende", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POLISHED_HORNBLENDE = registerWithTab("polished_hornblende", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_HORNBLENDE = registerWithTab("powdered_hornblende", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PYROXENE = registerWithTab("pyroxene", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POLISHED_PYROXENE = registerWithTab("polished_pyroxene", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_PYROXENE = registerWithTab("powdered_pyroxene", () -> new Item(basicItem()));
    public static final RegistryObject<Item> NITER = registerWithTab("niter", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_NITER = registerWithTab("powdered_niter", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_COAL = registerWithTab("powdered_coal", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_CHARCOAL = registerWithTab("powdered_charcoal", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_LAPIS_LAZULI = registerWithTab("powdered_lapis_lazuli", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ZINC_PLATE = registerWithTab("zinc_plate", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PEBBLE_BLOCK = registerWithTab("pebble_block", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PYROXENE_GLASS_PANE = registerWithTab("pyroxene_glass_pane", () -> new Item(basicItem()));
    public static final RegistryObject<Item> GEOGLOGICAL_HAMMER = registerWithTab("geological_hammer", () -> new Item(basicItem()));
    public static final RegistryObject<Item> ICE_FRAGMENTS = registerWithTab("ice_fragments", () -> new Item(basicItem()));
    public static final RegistryObject<Item> PACKED_ICE_FRAGMENTS = registerWithTab("packed_ice_fragments", () -> new Item(basicItem()));
    public static final RegistryObject<Item> BLUE_ICE_FRAGMENTS = registerWithTab("blue_ice_fragments", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_IRON_ORE = registerWithTab("powdered_iron_ore", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_COPPER_ORE = registerWithTab("powdered_copper_ore", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_GOLD_ORE = registerWithTab("powdered_gold_ore", () -> new Item(basicItem()));
    public static final RegistryObject<Item> POWDERED_ZINC_ORE = registerWithTab("powdered_zinc_ore", () -> new Item(basicItem()));
    public static final RegistryObject<Item> SALT = registerWithTab("salt", () -> new Item(basicItem()));
    public static final RegistryObject<Item> BATTER = registerWithTab("batter", () -> new Item(basicItem()));

    // Foods
    public static final RegistryObject<Item> PANCAKE = registerWithTab("pancake", () -> new Item(foodItem(new FoodProperties.Builder().nutrition(12).saturationMod(19.2f).build())));
    public static final RegistryObject<Item> JAM_PANCAKE = registerWithTab("jam_pancake", () -> new Item(foodItem(new FoodProperties.Builder().nutrition(14).saturationMod(21f).build())));
    public static final RegistryObject<Item> HONEY_PANCAKE = registerWithTab("honey_pancake", () -> new Item(foodItem(new FoodProperties.Builder().nutrition(14).saturationMod(21f).build())));
    public static final RegistryObject<Item> CHOCOLATE_PANCAKE = registerWithTab("chocolate_pancake", () -> new Item(foodItem(new FoodProperties.Builder().nutrition(14).saturationMod(21f).build())));

    // Block Items
    public static final RegistryObject<Item> SAND_PILE = registerWithTab("sand_pile", () -> new BlockItem(BlockRegistry.SAND_PILE.get(), basicItem()));
    public static final RegistryObject<Item> SNOW_PILE = registerWithTab("snow_pile", () -> new BlockItem(BlockRegistry.SNOW_PILE.get(), basicItem()));
    public static final RegistryObject<Item> ASH_PILE = registerWithTab("ash_pile", () -> new BlockItem(BlockRegistry.ASH_PILE.get(), basicItem()));
    public static final RegistryObject<Item> QUARTZITE = registerWithTab("quartzite", () -> new BlockItem(BlockRegistry.QUARTZITE.get(), basicItem()));
    public static final RegistryObject<Item> MARBLE_BLOCK = registerWithTab("marble_block", () -> new BlockItem(BlockRegistry.MARBLE_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> PEBBLE_PATH = registerWithTab("pebble_path", () -> new BlockItem(BlockRegistry.PEBBLE_PATH.get(), basicItem()));
    public static final RegistryObject<Item> QUARTZITE_PATH = registerWithTab("quartzite_path", () -> new BlockItem(BlockRegistry.QUARTZITE_PATH.get(), basicItem()));
    public static final RegistryObject<Item> MARBLE_PATH = registerWithTab("marble_path", () -> new BlockItem(BlockRegistry.MARBLE_PATH.get(), basicItem()));
    public static final RegistryObject<Item> PYROXENE_GLASS = registerWithTab("pyroxene_glass", () -> new BlockItem(BlockRegistry.PYROXENE_GLASS.get(), basicItem()));
    public static final RegistryObject<Item> LUMINOUS_MIRROR = registerWithTab("luminous_mirror", () -> new BlockItem(BlockRegistry.LUMINOUS_MIRROR.get(), basicItem()));
    public static final RegistryObject<Item> ELECTRIC_BURNER_ITEM = registerWithTab("electric_burner", () -> new BlockItem(BlockRegistry.ELECTRIC_BURNER.get(), new Item.Properties()));
    public static final RegistryObject<Item> SOLAR_HEATER = registerWithTab("solar_heater", () -> new BlockItem(BlockRegistry.SOLAR_HEATER.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_ICE = registerWithTab("cracked_ice", () -> new BlockItem(BlockRegistry.CRACKED_ICE.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_PACKED_ICE = registerWithTab("cracked_packed_ice", () -> new BlockItem(BlockRegistry.CRACKED_PACKED_ICE.get(), basicItem()));
    public static final RegistryObject<Item> CRACKED_BLUE_ICE = registerWithTab("cracked_blue_ice", () -> new BlockItem(BlockRegistry.CRACKED_BLUE_ICE.get(), basicItem()));
    public static final RegistryObject<Item> ORE_BEARING_GLACIAL_ICE = registerWithTab("ore_bearing_glacial_ice", () -> new BlockItem(BlockRegistry.ORE_BEARING_GLACIAL_ICE.get(), basicItem()));
    public static final RegistryObject<Item> PERMAFROST = registerWithTab("permafrost", () -> new BlockItem(BlockRegistry.PERMAFROST.get(), basicItem()));
    public static final RegistryObject<Item> SALT_BLOCK = registerWithTab("salt_block", () -> new BlockItem(BlockRegistry.SALT_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> SALINE_SOIL = registerWithTab("saline_soil", () -> new BlockItem(BlockRegistry.SALINE_SOIL.get(), basicItem()));
    public static final RegistryObject<Item> SALINE_MUD = registerWithTab("saline_mud", () -> new BlockItem(BlockRegistry.SALINE_MUD.get(), basicItem()));
    public static final RegistryObject<Item> SALINE_FARMLAND = registerWithTab("saline_farmland", () -> new BlockItem(BlockRegistry.SALINE_FARMLAND.get(), basicItem()));
    public static final RegistryObject<Item> BLACK_SOIL = registerWithTab("black_soil", () -> new BlockItem(BlockRegistry.BLACK_SOIL.get(), basicItem()));
    public static final RegistryObject<Item> BLACK_MUD = registerWithTab("black_mud", () -> new BlockItem(BlockRegistry.BLACK_MUD.get(), basicItem()));
    public static final RegistryObject<Item> BLACK_FARMLAND = registerWithTab("black_farmland", () -> new BlockItem(BlockRegistry.BLACK_FARMLAND.get(), basicItem()));
    public static final RegistryObject<Item> NITER_BLOCK = registerWithTab("niter_block", () -> new BlockItem(BlockRegistry.NITER_BLOCK.get(), basicItem()));
    public static final RegistryObject<Item> PAPER_MOLD = registerWithTab("paper_mold", () -> new BlockItem(BlockRegistry.PAPER_MOLD.get(), basicItem()));
    public static final RegistryObject<Item> PULP_FILLED_PAPER_MOLD = registerWithTab("pulp_filled_paper_mold", () -> new BlockItem(BlockRegistry.PULP_FILLED_PAPER_MOLD.get(), basicItem()));
    public static final RegistryObject<Item> PAPER_FILLED_PAPER_MOLD = registerWithTab("paper_filled_paper_mold", () -> new BlockItem(BlockRegistry.PAPER_FILLED_PAPER_MOLD.get(), basicItem()));
    public static final RegistryObject<Item> ANDESITE_FIRE_PIT = registerWithTab("andesite_fire_pit", () -> new BlockItem(BlockRegistry.ANDESITE_FIRE_PIT.get(), basicItem()));
    public static final RegistryObject<Item> BRASS_FIRE_PIT = registerWithTab("brass_fire_pit", () -> new BlockItem(BlockRegistry.BRASS_FIRE_PIT.get(), basicItem()));
    public static final RegistryObject<Item> MECHANICAL_FISHING_NET = registerWithTab("mechanical_fishing_net", () -> new BlockItem(BlockRegistry.MECHANICAL_FISHING_NET.get(), basicItem()));
}