package com.adonis;

import com.adonis.config.NaturalTransformConfig;
import com.adonis.transform.FarmlandTransformHandler;
import com.adonis.transform.NaturalTransformHandler;
import com.adonis.event.BasinFluidInteractionHandler;
import com.adonis.fluid.FluidInteraction;
import com.adonis.fluid.GeographyFluids;
import com.adonis.networking.ModMessages;
import com.adonis.registry.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

        // 注册配置
        NaturalTransformConfig.register();
        LOGGER.info("🔧 Create Geography configuration registered");

        // 注册所有内容
        registerContent(modEventBus);

        // 设置事件监听
        setupEventListeners(modEventBus);

        LOGGER.info("🚀 Create Geography initialized");
    }

    private void registerContent(IEventBus modEventBus) {
        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        TabRegistry.CREATIVE_TABS.register(modEventBus);
        SoundRegistry.SOUND_EVENTS.register(modEventBus);
        EntityRegistry.ENTITIES.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);

        GeographyFluids.register();
        REGISTRATE.registerEventListeners(modEventBus);
    }

    private void setupEventListeners(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this); // 燃料事件
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                // 注册流体交互和网络
                FluidInteraction.registerFluidInteractions();
                ModMessages.register();

                // 注册事件处理器
                MinecraftForge.EVENT_BUS.register(BasinFluidInteractionHandler.class);
                MinecraftForge.EVENT_BUS.register(NaturalTransformHandler.class);
                // 注册耕地转换处理器（虽然主要通过Mixin工作，但注册以备将来使用）
                MinecraftForge.EVENT_BUS.register(FarmlandTransformHandler.class);

                LOGGER.info("✅ Create Geography common setup completed");

                // 验证注册状态
                verifyRegistrations();

            } catch (Exception e) {
                LOGGER.error("❌ Error during common setup: ", e);
            }
        });
    }

    private void verifyRegistrations() {
        LOGGER.info("🔍 Verifying registrations:");

        // 验证关键方块
        verifyBlock("FROZEN_SOIL", BlockRegistry.FROZEN_SOIL);
        verifyBlock("CRACKED_ICE", BlockRegistry.CRACKED_ICE);
        verifyBlock("SALINE_MUD", BlockRegistry.SALINE_MUD);

        // 验证流体
        if (GeographyFluids.BRINE.get() != null) {
            LOGGER.info("✅ BRINE fluid registered");
        } else {
            LOGGER.error("❌ BRINE fluid registration failed");
        }

        LOGGER.info("🔍 Registration verification completed");
    }

    private void verifyBlock(String name, net.minecraftforge.registries.RegistryObject<?> registryObject) {
        if (registryObject != null && registryObject.get() != null) {
            LOGGER.info("✅ {} registered: {}", name, registryObject.get());
        } else {
            LOGGER.error("❌ {} registration failed", name);
        }
    }

    /**
     * 燃料注册事件
     */
    @SubscribeEvent
    public void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        try {
            var fuel = event.getItemStack();

            if (fuel.is(ItemRegistry.COAL_POWDER.get())) {
                event.setBurnTime(1600);
            } else if (fuel.is(ItemRegistry.CHARCOAL_POWDER.get())) {
                event.setBurnTime(1600);
            }
        } catch (Exception e) {
            LOGGER.error("Error setting fuel burn time: ", e);
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}