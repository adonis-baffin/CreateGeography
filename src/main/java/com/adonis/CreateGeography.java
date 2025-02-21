package com.adonis;

import com.adonis.config.ClientConfig;
import com.adonis.config.CommonConfig;
import com.adonis.datagen.DataGen;
import com.adonis.recipe.RecipeTypes;
import com.adonis.registry.BlockRegistry;
import com.adonis.registry.ItemRegistry;
import com.adonis.registry.SoundRegistry;
import com.adonis.registry.TabRegistry;
import com.adonis.recipe.FanProcessingTypes;
import com.adonis.util.BoilerHeaterRegistry;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    public static PartialModel DRAGON_MODEL;
//    public static final TagKey<Block> FAN_PROCESSING_CATALYSTS_HALITOSIS = BlockTags.create(new ResourceLocation(MODID, "fan_processing_catalysts/halitosis"));


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

        RecipeTypes.register(modEventBus);
        FanProcessingTypes.register();

        REGISTRATE.registerEventListeners(modEventBus);
        modEventBus.addListener(EventPriority.LOWEST, DataGen::gatherData);
        DRAGON_MODEL = new PartialModel(new ResourceLocation(CreateGeography.MODID, "dragon_head_export"));

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


