package com.adonis;

import com.adonis.event.BasinFluidInteractionHandler;
import com.adonis.event.FluidInteractionHandler;
import com.adonis.fluid.FluidInteraction;
import com.adonis.fluid.GeographyFluids;
import com.adonis.networking.ModMessages;
import com.adonis.registry.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
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
        LOGGER.info("CreateGeography 初始化开始");

        // 检查是否在数据生成模式
        boolean dataGen = System.getProperty("forge.data.gen") != null;
        if (dataGen) {
            LOGGER.info("检测到数据生成模式");
        }

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

        // 确保事件处理器类被加载（但在数据生成时跳过）
        if (!dataGen) {
            try {
                Class.forName("com.adonis.event.FluidInteractionHandler");
                Class.forName("com.adonis.event.BasinFluidInteractionHandler");
                LOGGER.info("事件处理器类已加载");
            } catch (ClassNotFoundException e) {
                LOGGER.error("无法加载事件处理器: ", e);
            }
        }

        LOGGER.info("CreateGeography 初始化完成");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FluidInteraction.registerFluidInteractions();
            ModMessages.register();

            // 显式注册事件处理器
            MinecraftForge.EVENT_BUS.register(FluidInteractionHandler.class);
            MinecraftForge.EVENT_BUS.register(BasinFluidInteractionHandler.class);
            LOGGER.info("事件处理器已注册到 Forge 事件总线");
        });
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