package com.adonis;

import com.adonis.config.NaturalTransformConfig;
import com.adonis.event.BasinFluidInteractionHandler;
import com.adonis.fluid.FluidInteraction;
import com.adonis.fluid.GeographyFluids;
import com.adonis.networking.ModMessages;
import com.adonis.registry.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateGeography.MODID)
public class CreateGeography {
    public static final String MODID = "creategeography";

    // 添加tooltip修饰器，与渔业模组保持一致
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }

    public CreateGeography() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 确保在注册其他内容之前设置好registrate
        REGISTRATE.registerEventListeners(modEventBus);

        // 注册配置
        NaturalTransformConfig.register();

        // 注册所有内容
        registerContent(modEventBus);

        // 设置事件监听
        setupEventListeners(modEventBus);
    }

    private void registerContent(IEventBus modEventBus) {
        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        TabRegistry.CREATIVE_TABS.register(modEventBus);
        SoundRegistry.SOUND_EVENTS.register(modEventBus);
//        EntityRegistry.ENTITIES.register(modEventBus);
//        BlockEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);

        GeographyFluids.register();
        // 移动到构造函数中，避免重复注册
        // REGISTRATE.registerEventListeners(modEventBus);
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

            } catch (Exception e) {
            }
        });
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
        }
    }
}