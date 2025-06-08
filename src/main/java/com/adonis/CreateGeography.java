package com.adonis;

import com.adonis.event.IndustrialBlockInteractionHandler;
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

@Mod(CreateGeography.MODID)
public class CreateGeography {
    public static final String MODID = "creategeography";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public CreateGeography() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册方块和物品
        REGISTRATE.registerEventListeners(modEventBus);
        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        TabRegistry.CREATIVE_TABS.register(modEventBus);
        SoundRegistry.SOUND_EVENTS.register(modEventBus);
        EntityRegistry.ENTITIES.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus); // 新增
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);       // 新增

        // 注册流体
        GeographyFluids.register();

        // 注册事件处理器（只处理堆肥桶批量堆肥，溜槽连接由Mixin处理）
        MinecraftForge.EVENT_BUS.register(IndustrialBlockInteractionHandler.class);

        REGISTRATE.registerEventListeners(modEventBus);
        modEventBus.addListener(this::commonSetup);
        GeographyPartialModels.initiate();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FluidInteraction.registerFluidInteractions();
            ModMessages.register(); // <--- 新增
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