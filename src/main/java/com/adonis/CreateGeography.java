package com.adonis;

import com.adonis.config.ClientConfig;
import com.adonis.config.CommonConfig;
import com.adonis.content.block.IndustrialComposterBlock;
import com.adonis.data.TagsProvider;
import com.adonis.datagen.DataGen;
//import com.adonis.event.BowEvents;
import com.adonis.fluid.FluidInteraction;
import com.adonis.fluid.GeographyFluids;
import com.adonis.recipe.FanProcessingTypes;
import com.adonis.recipe.RecipeTypes;
import com.adonis.registry.*;
import com.adonis.utils.BoilerHeaterRegistry;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
    public static PartialModel DRAGON_MODEL;

    public CreateGeography() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册方块和物品
        REGISTRATE.registerEventListeners(modEventBus);  // 初始化 Registrate
        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        TabRegistry.CREATIVE_TABS.register(modEventBus);
        SoundRegistry.SOUND_EVENTS.register(modEventBus);
        EntityRegistry.ENTITIES.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);


        // 注册流体
        GeographyFluids.register();

        // 注册配置
        CommonConfig.registerCommonConfig();
        ClientConfig.registerClientConfig();

        // 注册事件总线
        MinecraftForge.EVENT_BUS.register(this);

        TagsProvider.registerTags();

        // 注册配方和处理器
        RecipeTypes.register(modEventBus);
        FanProcessingTypes.register();

        // 注册Registrate事件监听器
        REGISTRATE.registerEventListeners(modEventBus);
        modEventBus.addListener(EventPriority.LOWEST, DataGen::gatherData);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
//        MinecraftForge.EVENT_BUS.register(BowEvents.class);

        // 注册模型
        DRAGON_MODEL = new PartialModel(new ResourceLocation(MODID, "dragon_head_export"));
        GeographyPartialModels.initiate();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BoilerHeaterRegistry.registerBoilerHeaters();
            FluidInteraction.registerFluidInteractions();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(GeographyRenderers::setupRenderers); // 明确调用静态方法
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (state.getBlock() instanceof IndustrialComposterBlock && event.getEntity().isShiftKeyDown()) {
            if (!event.getLevel().isClientSide) {
                ((IndustrialComposterBlock) state.getBlock()).bulkCompost(
                        state, event.getLevel(), event.getPos(), event.getEntity(), event.getHand(), event.getHitVec()
                );
            }
            event.setCanceled(true);
            event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // 客户端设置（如果需要额外逻辑可以在这里添加）
        }
    }


    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}