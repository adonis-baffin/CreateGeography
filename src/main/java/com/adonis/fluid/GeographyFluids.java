package com.adonis.fluid;

import com.adonis.CreateGeography;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import com.tterrag.registrate.util.entry.FluidEntry;

import java.util.function.Consumer;

import static com.adonis.CreateGeography.MODID;
import static com.adonis.CreateGeography.REGISTRATE;

public class GeographyFluids {

    public static final FluidEntry<ForgeFlowingFluid.Flowing> BRINE =
            REGISTRATE.fluid("brine",
                            new ResourceLocation("minecraft", "block/water_still"),
                            new ResourceLocation("minecraft", "block/water_flow"),
                            (properties, stillTexture, flowingTexture) -> new FluidType(properties) {
                                @Override
                                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                                    consumer.accept(new IClientFluidTypeExtensions() {
                                        @Override
                                        public ResourceLocation getStillTexture() {
                                            return stillTexture;
                                        }

                                        @Override
                                        public ResourceLocation getFlowingTexture() {
                                            return flowingTexture;
                                        }

                                        @Override
                                        public int getTintColor() {
                                            // 几个颜色选项（都设置为60%透明度）：
                                            return 0x99e2dc8b; // 淡紫色（薰衣草色）
                                        }
                                    });
                                }
                            })
                    .lang("Brine")
                    .properties(b -> b.viscosity(1500).density(500))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .renderType(() -> RenderType.translucent()) // 添加透明渲染层
                    .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> ASH_WATER =
            REGISTRATE.fluid("ash_water",
                            new ResourceLocation("minecraft", "block/water_still"),
                            new ResourceLocation("minecraft", "block/water_flow"),
                            (properties, stillTexture, flowingTexture) -> new FluidType(properties) {
                                @Override
                                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                                    consumer.accept(new IClientFluidTypeExtensions() {
                                        @Override
                                        public ResourceLocation getStillTexture() {
                                            return stillTexture;
                                        }

                                        @Override
                                        public ResourceLocation getFlowingTexture() {
                                            return flowingTexture;
                                        }

                                        @Override
                                        public int getTintColor() {
                                            return 0x99708090; // 半透明的石板灰
                                            // return 0x99A9A9A9; // 深灰色
                                            // return 0x99696969; // 暗灰色
                                        }
                                    });
                                }
                            })
                    .lang("Ash Water")
                    .properties(b -> b.viscosity(1200).density(1050))
                    .fluidProperties(p -> p.levelDecreasePerBlock(1)
                            .tickRate(20)
                            .slopeFindDistance(4)
                            .explosionResistance(100f))
                    .renderType(() -> RenderType.translucent()) // 添加透明渲染层
                    .register();

    public static void register() {
        // 这个方法被调用时会触发所有静态字段的初始化
    }
}