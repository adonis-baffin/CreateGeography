package com.adonis.transform;

import com.adonis.config.NaturalTransformConfig;
import com.adonis.transform.TransformConditions;
import com.adonis.CreateGeography;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.adonis.fluid.GeographyFluids.BRINE;
import static com.adonis.fluid.GeographyFluids.ASH_WATER;
import static com.adonis.registry.BlockRegistry.*;

/**
 * 耕地转换处理器 - 处理所有耕地类型的转换和属性变化
 * 通过监听randomTick事件来处理转换
 */
@Mod.EventBusSubscriber(modid = CreateGeography.MODID)
public class FarmlandTransformHandler {

    // 自定义属性（假设这些在你的方块类中定义）
    private static final IntegerProperty FERTILITY = IntegerProperty.create("fertility", 0, 3);
    private static final IntegerProperty SALINITY = IntegerProperty.create("salinity", 0, 3);

    /**
     * 监听方块的randomTick事件
     */
    @SubscribeEvent
    public static void onBlockRandomTick(BlockEvent.NeighborNotifyEvent event) {
        // 这个事件不适合，我们需要其他方法
        // 让我们直接在方块类中处理，或者使用Mixin但更简洁的方式
    }

    /**
     * 处理耕地的随机tick转换
     * 这个方法会被重构后的方块类调用
     */
    public static boolean processRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!NaturalTransformConfig.isFarmlandTransformsEnabled()) {
            return false; // 继续原版逻辑
        }

        try {
            Block currentBlock = state.getBlock();

            // 检查是否为支持的耕地类型
            if (!TransformConditions.FarmlandTransformConditions.canTransformFarmland(level, pos, currentBlock)) {
                return false;
            }

            // 获取流体环境
            Fluid brineFluid = BRINE.get() != null ? BRINE.get().getSource() : null;
            Fluid ashWaterFluid = ASH_WATER.get() != null ? ASH_WATER.get().getSource() : null;

            TransformConditions.FarmlandTransformConditions.FluidEnvironment environment =
                    TransformConditions.FarmlandTransformConditions.getFarmlandFluidEnvironment(level, pos, brineFluid, ashWaterFluid);

            // 处理湿润度（优先级最高）
            handleMoisture(state, level, pos, environment);

            // 处理转换逻辑
            return handleTransformations(state, level, pos, random, currentBlock, environment);

        } catch (Exception e) {
            if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                CreateGeography.LOGGER.error("Error in farmland transform at {}: ", pos, e);
            }
            return false;
        }
    }

    /**
     * 处理湿润度逻辑 - 强化版本，确保自定义流体能维持耕地
     */
    private static void handleMoisture(BlockState state, ServerLevel level, BlockPos pos,
                                       TransformConditions.FarmlandTransformConditions.FluidEnvironment environment) {
        if (!state.hasProperty(BlockStateProperties.MOISTURE)) return;

        int currentMoisture = state.getValue(BlockStateProperties.MOISTURE);

        // 🚨 关键修复：如果附近有任何流体（水、盐水、灰水）或在下雨，强制保持最大湿润度
        if (environment.canHydrate() || level.isRainingAt(pos.above())) {
            if (currentMoisture < 7) {
                level.setBlock(pos, state.setValue(BlockStateProperties.MOISTURE, 7), 2);

                if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                    CreateGeography.LOGGER.debug("💧 Moisturized farmland at {} to 7 (brine: {}, ash: {}, water: {})",
                            pos, environment.nearBrine, environment.nearAshWater, environment.nearWater);
                }
            }
        }
        // 🚨 额外保护：即使environment显示没有流体，也要再次检查自定义流体
        else if (hasDirectCustomFluidContact(level, pos)) {
            if (currentMoisture < 7) {
                level.setBlock(pos, state.setValue(BlockStateProperties.MOISTURE, 7), 2);

                if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                    CreateGeography.LOGGER.debug("💧 Emergency moisturization by direct custom fluid contact at {}", pos);
                }
            }
        }
    }

    /**
     * 直接检查是否有自定义流体接触 - 作为额外保护
     */
    private static boolean hasDirectCustomFluidContact(ServerLevel level, BlockPos pos) {
        // 检查紧邻的6个方向
        BlockPos[] adjacent = {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.above(), pos.below()
        };

        for (BlockPos checkPos : adjacent) {
            var fluidState = level.getFluidState(checkPos);
            if (!fluidState.isEmpty()) {
                var fluidType = fluidState.getType();

                // 检查盐水
                if (BRINE.get() != null &&
                        (fluidType == BRINE.get().getSource() || fluidType == BRINE.get().getFlowing())) {
                    return true;
                }

                // 检查灰水
                if (ASH_WATER.get() != null &&
                        (fluidType == ASH_WATER.get().getSource() || fluidType == ASH_WATER.get().getFlowing())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理转换逻辑
     */
    private static boolean handleTransformations(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
                                                 Block currentBlock, TransformConditions.FarmlandTransformConditions.FluidEnvironment environment) {

        // 普通耕地转换
        if (currentBlock == Blocks.FARMLAND) {
            return handleNormalFarmlandTransform(state, level, pos, random, environment);
        }
        // 黑土耕地转换
        else if (isBlackFarmlandBlock(currentBlock)) {
            return handleBlackFarmlandTransform(state, level, pos, random, environment);
        }
        // 盐碱耕地转换
        else if (isSalineFarmlandBlock(currentBlock)) {
            return handleSalineFarmlandTransform(state, level, pos, random, environment);
        }

        return false; // 继续原版逻辑
    }

    /**
     * 处理普通耕地转换
     */
    private static boolean handleNormalFarmlandTransform(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
                                                         TransformConditions.FarmlandTransformConditions.FluidEnvironment environment) {

        Block currentBlock = state.getBlock();
        Block targetBlock = null;
        double probability = 0.0;

        if (environment.nearBrine && SALINE_FARMLAND.get() != null) {
            // 优先转换为盐碱耕地
            targetBlock = SALINE_FARMLAND.get();
            probability = NaturalTransformConfig.FARMLAND_TO_SALINE_FARMLAND_PROBABILITY.get();
        } else if (environment.nearAshWater && BLACK_FARMLAND.get() != null) {
            // 转换为黑土耕地
            targetBlock = BLACK_FARMLAND.get();
            probability = NaturalTransformConfig.FARMLAND_TO_BLACK_FARMLAND_PROBABILITY.get();
        }

        if (targetBlock != null && random.nextFloat() < probability) {
            int moisture = state.getValue(BlockStateProperties.MOISTURE);
            BlockState newState = targetBlock.defaultBlockState().setValue(BlockStateProperties.MOISTURE, moisture);

            // 设置初始属性
            if (targetBlock == SALINE_FARMLAND.get() && newState.hasProperty(SALINITY)) {
                newState = newState.setValue(SALINITY, 1); // 初始盐碱化等级
            } else if (targetBlock == BLACK_FARMLAND.get() && newState.hasProperty(FERTILITY)) {
                newState = newState.setValue(FERTILITY, 1); // 初始肥力等级
            }

            level.setBlock(pos, newState, 3);
            logTransformation("Normal Farmland Transform", currentBlock, targetBlock, pos);
            return true; // 取消原版逻辑
        }

        return false; // 继续原版逻辑
    }

    /**
     * 处理黑土耕地转换
     */
    private static boolean handleBlackFarmlandTransform(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
                                                        TransformConditions.FarmlandTransformConditions.FluidEnvironment environment) {

        Block currentBlock = state.getBlock();

        if (environment.nearBrine && SALINE_FARMLAND.get() != null) {
            // 转换为盐碱耕地
            double probability = NaturalTransformConfig.BLACK_FARMLAND_TO_SALINE_PROBABILITY.get();

            if (random.nextFloat() < probability) {
                int moisture = state.getValue(BlockStateProperties.MOISTURE);
                BlockState newState = SALINE_FARMLAND.get().defaultBlockState()
                        .setValue(BlockStateProperties.MOISTURE, moisture);

                if (newState.hasProperty(SALINITY)) {
                    newState = newState.setValue(SALINITY, 1);
                }

                level.setBlock(pos, newState, 3);
                logTransformation("Black Farmland to Saline", currentBlock, SALINE_FARMLAND.get(), pos);
                return true;
            }
        } else if (environment.nearAshWater && state.hasProperty(FERTILITY)) {
            // 增加肥力
            double probability = NaturalTransformConfig.BLACK_FARMLAND_FERTILITY_GAIN_PROBABILITY.get();

            if (random.nextFloat() < probability) {
                int currentFertility = state.getValue(FERTILITY);
                if (currentFertility < 3) {
                    level.setBlock(pos, state.setValue(FERTILITY, currentFertility + 1), 2);

                    if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                        CreateGeography.LOGGER.debug("Increased fertility at {} to {}", pos, currentFertility + 1);
                    }
                }
            }
        }

        return false; // 继续原版逻辑
    }

    /**
     * 处理盐碱耕地转换
     */
    private static boolean handleSalineFarmlandTransform(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
                                                         TransformConditions.FarmlandTransformConditions.FluidEnvironment environment) {

        Block currentBlock = state.getBlock();

        if (!state.hasProperty(SALINITY)) {
            return false;
        }

        int currentSalinity = state.getValue(SALINITY);

        if (environment.nearBrine) {
            // 增加盐碱化程度
            double probability = NaturalTransformConfig.SALINE_FARMLAND_SALINITY_GAIN_PROBABILITY.get();

            if (random.nextFloat() < probability && currentSalinity < 3) {
                level.setBlock(pos, state.setValue(SALINITY, currentSalinity + 1), 2);

                if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                    CreateGeography.LOGGER.debug("Increased salinity at {} to {}", pos, currentSalinity + 1);
                }
            }
        } else if (environment.nearWater || environment.nearAshWater) {
            // 降低盐碱化程度
            double probability = NaturalTransformConfig.SALINE_FARMLAND_SALINITY_LOSS_PROBABILITY.get();

            if (random.nextFloat() < probability && currentSalinity > 0) {
                int newSalinity = currentSalinity - 1;

                if (newSalinity == 0) {
                    // 完全去除盐碱化，变回普通耕地
                    int moisture = state.getValue(BlockStateProperties.MOISTURE);
                    BlockState newState = Blocks.FARMLAND.defaultBlockState()
                            .setValue(BlockStateProperties.MOISTURE, moisture);

                    level.setBlock(pos, newState, 3);
                    logTransformation("Saline Farmland Cleaned", currentBlock, Blocks.FARMLAND, pos);
                    return true;
                } else {
                    level.setBlock(pos, state.setValue(SALINITY, newSalinity), 2);

                    if (NaturalTransformConfig.isDebugLoggingEnabled()) {
                        CreateGeography.LOGGER.debug("Decreased salinity at {} to {}", pos, newSalinity);
                    }
                }
            }
        }

        return false; // 继续原版逻辑
    }

    // ==================== 工具方法 ====================

    private static boolean isBlackFarmlandBlock(Block block) {
        return BLACK_FARMLAND.get() != null && block == BLACK_FARMLAND.get();
    }

    private static boolean isSalineFarmlandBlock(Block block) {
        return SALINE_FARMLAND.get() != null && block == SALINE_FARMLAND.get();
    }

    private static void logTransformation(String transformType, Block sourceBlock, Block targetBlock, BlockPos pos) {
        if (NaturalTransformConfig.isDebugLoggingEnabled()) {
            CreateGeography.LOGGER.info("🌾 {}: {} -> {} at {}",
                    transformType, sourceBlock, targetBlock, pos);
        }
    }
}