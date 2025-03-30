package com.adonis;

import com.adonis.config.ClientConfig;
import com.adonis.config.CommonConfig;
import com.adonis.datagen.DataGen;
import com.adonis.fluid.FluidInteraction;
import com.adonis.fluid.GeographyFluids;
import com.adonis.recipe.RecipeTypes;
import com.adonis.registry.*;
import com.adonis.recipe.FanProcessingTypes;
import com.adonis.utils.BoilerHeaterRegistry;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(CreateGeography.MODID)
public class CreateGeography {
    public static final String MODID = "creategeography";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    public static PartialModel DRAGON_MODEL;

    public CreateGeography() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册方块和物品
        BlockRegistry.BLOCKS.register(modEventBus);

        ItemRegistry.ITEMS.register(modEventBus);
        TabRegistry.CREATIVE_TABS.register(modEventBus);
        SoundRegistry.SOUND_EVENTS.register(modEventBus);
        // 注册实体
        EntityRegistry.ENTITIES.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus); // 使用 BlockEntityRegistry

        // 注册流体
        GeographyFluids.register();

        // 注册配置
        CommonConfig.registerCommonConfig();
        ClientConfig.registerClientConfig();
        MinecraftForge.EVENT_BUS.register(this);

        // 注册配方和处理器
        RecipeTypes.register(modEventBus);
        FanProcessingTypes.register();

        // 注册Registrate事件监听器
        REGISTRATE.registerEventListeners(modEventBus);
        modEventBus.addListener(EventPriority.LOWEST, DataGen::gatherData);
        modEventBus.addListener(this::commonSetup);

        // 注册模型
        DRAGON_MODEL = new PartialModel(new ResourceLocation(MODID, "dragon_head_export"));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BoilerHeaterRegistry.registerBoilerHeaters();
            FluidInteraction.registerFluidInteractions();
            LOGGER.info("Registered fluids:");
            LOGGER.info("Brine fluid registered: " +
                    ForgeRegistries.FLUIDS.getKey(GeographyFluids.BRINE.getSource()));
            LOGGER.info("Grey Water fluid registered: " +
                    ForgeRegistries.FLUIDS.getKey(GeographyFluids.GREY_WATER.getSource()));
            LOGGER.info("Mud fluid registered: " +
                    ForgeRegistries.FLUIDS.getKey(GeographyFluids.MUD.getSource()));
            LOGGER.info("Sand Slurry fluid registered: " +
                    ForgeRegistries.FLUIDS.getKey(GeographyFluids.SAND_SLURRY.getSource()));
            LOGGER.info("Tannin fluid registered: " +
                    ForgeRegistries.FLUIDS.getKey(GeographyFluids.TANNIN.getSource()));
            LOGGER.info("Battery Waste fluid registered: " +
                    ForgeRegistries.FLUIDS.getKey(GeographyFluids.BATTERY_WASTE.getSource()));
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
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