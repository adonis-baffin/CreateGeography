package com.adonis.creategeography.event;

import com.adonis.creategeography.config.NaturalTransformConfig;
import com.adonis.creategeography.content.block.SalineFarmlandBlock;
import com.adonis.creategeography.fluids.GeographyFluids;
import com.adonis.creategeography.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class NaturalTransformHandler {

    // 处理冰块的自然转换（与水结冰条件对齐）
    public static void handleIceTransformation(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!NaturalTransformConfig.ENABLE_FROZEN_SOIL_TRANSFORM.get()) {
            return;
        }

        // 检查是否为冰块
        if (state.is(BlockTags.ICE) || state.getBlock() instanceof IceBlock) {
            int range = NaturalTransformConfig.FROZEN_SOIL_TRANSFORM_RANGE.get();

            // 检查周围的方块
            for (BlockPos nearbyPos : BlockPos.betweenClosed(
                    pos.offset(-range, -1, -range),
                    pos.offset(range, 1, range))) {

                if (nearbyPos.equals(pos)) continue;

                BlockState nearbyState = level.getBlockState(nearbyPos);

                // 检查是否为可转换的土壤类方块，并且满足结冰条件
                if (isConvertibleSoilBlock(nearbyState) &&
                        canFreeze(level, nearbyPos) &&
                        random.nextFloat() < NaturalTransformConfig.FROZEN_SOIL_TRANSFORM_CHANCE.get()) {

                    level.setBlock(nearbyPos, BlockRegistry.FROZEN_SOIL.get().defaultBlockState(), 2);
                }
            }
        }
    }

    // 检查是否为可转换为冻土的土壤方块
    private static boolean isConvertibleSoilBlock(BlockState state) {
        return state.is(Blocks.DIRT) ||          // 泥土
                state.is(Blocks.COARSE_DIRT) ||   // 砂土
                state.is(Blocks.ROOTED_DIRT) ||   // 缠根泥土
                state.is(Blocks.GRASS_BLOCK);     // 草方块
    }

    // 检查位置是否满足结冰条件（与水结冰条件对齐）
    private static boolean canFreeze(ServerLevel level, BlockPos pos) {
        // 1. 超暖维度不能结冰
        if (level.dimensionType().ultraWarm()) {
            return false;
        }

        // 2. 生物群系温度检查
        var biome = level.getBiome(pos).value();
        float temperature = biome.getBaseTemperature();
        if (temperature > 0.15f) { // 与水结冰的温度阈值对齐
            return false;
        }

        // 3. 光照等级检查（光照太强不能结冰）
        int lightLevel = level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos);
        if (lightLevel > 10) { // 光照过强时不结冰
            return false;
        }

        // 4. 检查上方是否有透明方块或空气（与水结冰逻辑对齐）
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (!aboveState.isAir() && !aboveState.canOcclude()) {
            // 上方有实体方块时，需要更严格的条件
            return temperature < 0.0f;
        }

        return true;
    }

    // 处理盐碱耕地的脱盐（在盐碱耕地随机刻时调用）
    public static void handleSalineFarmlandDesalination(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getBlock() != BlockRegistry.SALINE_FARMLAND.get()) {
            return;
        }

        int salinity = state.getValue(SalineFarmlandBlock.SALINITY);
        if (salinity <= 0) {
            // 盐度为0时转换回普通耕地
            int moisture = state.getValue(SalineFarmlandBlock.MOISTURE);
            level.setBlock(pos, Blocks.FARMLAND.defaultBlockState()
                    .setValue(FarmBlock.MOISTURE, moisture), 3); // 修复：使用 flag 3 保持一致性
            return;
        }

        // 检查周围是否有普通水（非盐水）
        boolean hasWaterNearby = false;
        boolean hasBrineNearby = false;

        // 使用与盐碱化相同的范围配置
        int range = NaturalTransformConfig.BRINE_TRANSFORM_RANGE.get();

        for (BlockPos nearbyPos : BlockPos.betweenClosed(
                pos.offset(-range, -1, -range),
                pos.offset(range, 1, range))) {

            FluidState fluidState = level.getFluidState(nearbyPos);

            if (fluidState.is(Fluids.WATER)) {
                hasWaterNearby = true;
            } else if (fluidState.is(GeographyFluids.BRINE.get().getSource())) {
                hasBrineNearby = true;
                break; // 如果有盐水就不脱盐
            }
        }

        // 只有在有普通水且没有盐水的情况下才脱盐
        if (hasWaterNearby && !hasBrineNearby && random.nextFloat() < 0.1f) {
            level.setBlock(pos, state.setValue(SalineFarmlandBlock.SALINITY, salinity - 1), 3); // 修复：使用 flag 3 保持一致性
        }
    }

// 在 NaturalTransformHandler 类中添加以下方法：

    // 处理盐碱土和盐碱泥巴的脱盐（在对应方块随机刻时调用）
    public static void handleSalineSoilDesalination(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getBlock() != BlockRegistry.SALINE_DIRT.get() &&
                state.getBlock() != BlockRegistry.SALINE_MUD.get()) {
            return;
        }

        // 检查周围是否有普通水（非盐水）
        boolean hasWaterNearby = false;
        boolean hasBrineNearby = false;

        // 使用与盐碱化相同的范围配置
        int range = NaturalTransformConfig.BRINE_TRANSFORM_RANGE.get();

        for (BlockPos nearbyPos : BlockPos.betweenClosed(
                pos.offset(-range, -1, -range),
                pos.offset(range, 1, range))) {

            FluidState fluidState = level.getFluidState(nearbyPos);

            if (fluidState.is(Fluids.WATER)) {
                hasWaterNearby = true;
            } else if (fluidState.is(GeographyFluids.BRINE.get().getSource())) {
                hasBrineNearby = true;
                break; // 如果有盐水就不脱盐
            }
        }

        // 只有在有普通水且没有盐水的情况下才脱盐
        if (hasWaterNearby && !hasBrineNearby && random.nextFloat() < 0.05f) { // 5%概率脱盐，比耕地稍慢
            if (state.getBlock() == BlockRegistry.SALINE_DIRT.get()) {
                // 盐碱土脱盐变回普通泥土
                // 使用 flag 3 (1 + 2) 确保触发方块更新和发送到客户端，这样上方的盐晶会被检查并掉落
                level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
            } else if (state.getBlock() == BlockRegistry.SALINE_MUD.get()) {
                // 盐碱泥巴脱盐变回普通泥巴
                // 使用 flag 3 (1 + 2) 确保触发方块更新和发送到客户端，这样上方的盐晶会被检查并掉落
                level.setBlock(pos, Blocks.MUD.defaultBlockState(), 3);
            }
        }
    }

    // 处理盐晶生成（通用方法）
    public static void handleSaltCrystalGeneration(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!NaturalTransformConfig.ENABLE_SALT_CRYSTAL_GENERATION.get()) {
            return;
        }

        // 只在盐碱土和盐碱泥巴上生成盐晶
        if (state.getBlock() != BlockRegistry.SALINE_DIRT.get() &&
                state.getBlock() != BlockRegistry.SALINE_MUD.get()) {
            return;
        }

        BlockPos abovePos = pos.above();
        if (!level.isEmptyBlock(abovePos)) {
            return;
        }

        boolean hasBrineNearby = false;
        int range = NaturalTransformConfig.SALT_CRYSTAL_GENERATION_RANGE.get();

        searchLoop:
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                // 计算平面曼哈顿距离
                if (Math.abs(dx) + Math.abs(dz) <= range) {
                    BlockPos checkPos = pos.offset(dx, 0, dz); // dy为0，确保在同一Y层
                    FluidState fluidState = level.getFluidState(checkPos);
                    if (fluidState.is(GeographyFluids.BRINE.get().getSource()) ||
                            fluidState.getType() == GeographyFluids.BRINE.get().getSource()) {
                        hasBrineNearby = true;
                        break searchLoop; // 找到一个就足够了，跳出所有循环
                    }
                }
            }
        }

        // 如果附近有盐水，则有配置概率在其上方生成一层盐晶
        if (hasBrineNearby && random.nextFloat() < NaturalTransformConfig.SALT_CRYSTAL_GENERATION_CHANCE.get()) {
            BlockState saltCrystalState = BlockRegistry.SALT_CRYSTAL.get().defaultBlockState();
            level.setBlock(abovePos, saltCrystalState, 3);
        }
    }

    // 处理盐碱土/泥巴的随机刻（在对应方块类中调用）
    public static void handleSalineSoilRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 尝试生成盐晶
        handleSaltCrystalGeneration(state, level, pos, random);

        // 处理周围方块的盐碱化传播
        if (random.nextFloat() < NaturalTransformConfig.BRINE_TRANSFORM_CHANCE.get() * 0.2f) { // 降低传播速率
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos adjacentPos = pos.relative(direction);
                BlockState adjacentState = level.getBlockState(adjacentPos);

                // 传播盐碱化
                if (adjacentState.is(Blocks.DIRT) && state.getBlock() == BlockRegistry.SALINE_DIRT.get()) {
                    level.setBlock(adjacentPos, BlockRegistry.SALINE_DIRT.get().defaultBlockState(), 2);
                } else if (adjacentState.is(Blocks.MUD) && state.getBlock() == BlockRegistry.SALINE_MUD.get()) {
                    level.setBlock(adjacentPos, BlockRegistry.SALINE_MUD.get().defaultBlockState(), 2);
                }
            }
        }
    }
}