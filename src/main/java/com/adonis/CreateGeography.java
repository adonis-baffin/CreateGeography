package com.adonis;

import com.adonis.event.BasinFluidInteractionHandler;
import com.adonis.fluid.FluidInteraction;
import com.adonis.fluid.GeographyFluids;
import com.adonis.networking.ModMessages;
import com.adonis.registry.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CreateGeography.MODID)
public class CreateGeography {
    public static final String MODID = "creategeography";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public CreateGeography() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册方块和物品
        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        TabRegistry.CREATIVE_TABS.register(modEventBus);
        SoundRegistry.SOUND_EVENTS.register(modEventBus);
        EntityRegistry.ENTITIES.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);

        // 注册流体
        GeographyFluids.register();

        // 只注册一次！
        REGISTRATE.registerEventListeners(modEventBus);
        modEventBus.addListener(this::commonSetup);

        // 注册燃料事件
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("CreateGeography mod initialized - runtime recipe registration enabled");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FluidInteraction.registerFluidInteractions();
            ModMessages.register();

            // 显式注册事件处理器
            MinecraftForge.EVENT_BUS.register(BasinFluidInteractionHandler.class);
        });
    }

    /**
     * 燃料注册事件
     */
    @SubscribeEvent
    public void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack fuel = event.getItemStack();

        // 煤粉：燃烧时间相当于煤炭 (1600 ticks)
        if (fuel.is(ItemRegistry.COAL_POWDER.get())) {
            event.setBurnTime(1600);
        }
        // 木炭粉：燃烧时间相当于煤炭 (1600 ticks)
        else if (fuel.is(ItemRegistry.CHARCOAL_POWDER.get())) {
            event.setBurnTime(1600);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}