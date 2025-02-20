package com.adonis;

import com.adonis.config.ClientConfig;
import com.adonis.config.CommonConfig;
import com.adonis.registry.BlockRegistry;
import com.adonis.registry.ItemRegistry;
import com.adonis.registry.SoundRegistry;
import com.adonis.registry.TabRegistry;
import com.adonis.util.BoilerHeaterRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreateGeography.MODID)
public class CreateGeography {
    public static final String MODID = "creategeography";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateGeography(){

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

//        modEventBus.addListener(this::commonSetup);
        BlockRegistry.BLOCKS.register(modEventBus);
        BlockRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        TabRegistry.CREATIVE_TABS.register(modEventBus);
        SoundRegistry.SOUND_EVENTS.register(modEventBus);

        CommonConfig.registerCommonConfig();
        ClientConfig.registerClientConfig();
        MinecraftForge.EVENT_BUS.register(this);

    }

//    private void commonSetup(final FMLCommonSetupEvent event) {
//        BoilerHeaterRegistry.registerBoilerHeaters();
//        Events.register(MinecraftForge.EVENT_BUS);
//    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //PonderIndex.register();
        }

    }
    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(CreateGeography.MODID, path);
    }

}


