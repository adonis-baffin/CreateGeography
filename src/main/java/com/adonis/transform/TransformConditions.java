package com.adonis.transform;

import com.adonis.config.NaturalTransformConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 转换条件检查类 - 负责验证各种转换的前置条件
 */
public class TransformConditions {

    // 自定义标签 - 这些需要在data包中定义
    public static final TagKey<Block> RAW_ORE_BLOCKS = BlockTags.create(new ResourceLocation("creategeography", "raw_ore_blocks"));
    public static final TagKey<Block> SOIL_BLOCKS = BlockTags.create(new ResourceLocation("creategeography", "soil_blocks"));
    public static final TagKey<Block> FREEZABLE_SOIL_BLOCKS = BlockTags.create(new ResourceLocation("creategeography", "freezable_soil_blocks"));

    /**
     * 冰破裂条件检查
     */
    public static class IceCrackingConditions {

        public static boolean canCrack(ServerLevel level, BlockPos pos, Block iceBlock) {
            if (!NaturalTransformConfig.isIceCrackingEnabled()) return false;
            if (!isIceBlock(iceBlock)) return false;

            // 寒冷群系检查
            if (NaturalTransformConfig.ICE_REQUIRE_COLD_BIOME.get()) {
                if (!isColdBiome(level, pos)) return false;
            }

            // 天空暴露检查
            if (NaturalTransformConfig.ICE_REQUIRE_SKY_ACCESS.get()) {
                if (!level.canSeeSky(pos)) return false;
            }

            // 下方粗矿块检查
            if (NaturalTransformConfig.ICE_REQUIRE_RAW_ORE_BELOW.get()) {
                if (!hasRawOreBelow(level, pos)) return false;
            }

            return true;
        }

        public static double getCrackProbability(ServerLevel level, BlockPos pos, Block targetCrackedBlock) {
            double baseProbability = NaturalTransformConfig.ICE_CRACK_BASE_PROBABILITY.get();

            // 检查X轴相邻是否有破裂冰
            if (hasAdjacentCrackedIceOnXAxis(level, pos, targetCrackedBlock)) {
                baseProbability += NaturalTransformConfig.ICE_CRACK_BOOST_PROBABILITY.get();
            }

            return Math.min(1.0, baseProbability);
        }

        private static boolean hasAdjacentCrackedIceOnXAxis(ServerLevel level, BlockPos pos, Block crackedBlock) {
            int range = NaturalTransformConfig.ICE_CRACK_BOOST_RANGE.get();

            for (int x = -range; x <= range; x++) {
                if (x != 0) {
                    BlockPos checkPos = pos.offset(x, 0, 0);
                    if (level.getBlockState(checkPos).getBlock() == crackedBlock) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static boolean hasRawOreBelow(ServerLevel level, BlockPos pos) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            return belowState.is(RAW_ORE_BLOCKS);
        }
    }

    /**
     * 土壤冻结条件检查
     */
    public static class SoilFreezingConditions {

        public static boolean canFreeze(ServerLevel level, BlockPos pos, Block soilBlock) {
            if (!NaturalTransformConfig.isSoilFreezingEnabled()) return false;
            if (!isFreezableSoilBlock(soilBlock)) return false;

            // 寒冷群系检查
            if (NaturalTransformConfig.SOIL_REQUIRE_COLD_BIOME.get()) {
                if (!isColdBiome(level, pos)) return false;
            }

            return true;
        }

        public static double getFreezeProbability(ServerLevel level, BlockPos pos) {
            double baseProbability = NaturalTransformConfig.SOIL_FREEZE_BASE_PROBABILITY.get();

            // 表层加成
            if (isSurface(level, pos)) {
                baseProbability += NaturalTransformConfig.SOIL_FREEZE_SURFACE_BOOST.get();
            }

            // 水/冰邻接加成
            if (hasWaterOrIceNearby(level, pos)) {
                baseProbability += NaturalTransformConfig.SOIL_FREEZE_WATER_ICE_BOOST.get();
            }

            return Math.min(1.0, baseProbability);
        }

        private static boolean isSurface(ServerLevel level, BlockPos pos) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            return aboveState.isAir() || !aboveState.isCollisionShapeFullBlock(level, abovePos);
        }

        private static boolean hasWaterOrIceNearby(ServerLevel level, BlockPos pos) {
            int range = NaturalTransformConfig.SOIL_FREEZE_WATER_ICE_RANGE.get();

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        BlockPos checkPos = pos.offset(x, y, z);
                        BlockState checkState = level.getBlockState(checkPos);

                        if (isIceBlock(checkState.getBlock()) ||
                                checkState.getFluidState().getType() == Fluids.WATER) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private static boolean isFreezableSoilBlock(Block block) {
            return block.defaultBlockState().is(FREEZABLE_SOIL_BLOCKS);
        }
    }

    /**
     * 盐碱化条件检查
     */
    public static class SalinizationConditions {

        public static boolean canSalinizeInMangrove(ServerLevel level, BlockPos pos, Block mudBlock) {
            if (!NaturalTransformConfig.isSalinizationEnabled()) return false;
            if (mudBlock != Blocks.MUD) return false;

            return isMangroveSwamp(level, pos);
        }

        public static double getMangroveSalinizationProbability(ServerLevel level, BlockPos pos, Block targetSalineBlock) {
            double baseProbability = NaturalTransformConfig.MANGROVE_SALINIZATION_PROBABILITY.get();

            // 检查X轴相邻是否有盐碱泥巴
            if (hasAdjacentSalineMudOnXAxis(level, pos, targetSalineBlock)) {
                baseProbability += NaturalTransformConfig.MANGROVE_SALINIZATION_BOOST_PROBABILITY.get();
            }

            return Math.min(1.0, baseProbability);
        }

        public static boolean canSalinizeNearBrine(ServerLevel level, BlockPos pos, Block block, Fluid brineFluid) {
            if (!NaturalTransformConfig.isSalinizationEnabled()) return false;
            if (brineFluid == null) return false;

            // 检查是否为适合的方块类型
            if (!(isSoilBlock(block) || block == Blocks.MUD)) return false;

            // 检查盐水范围
            return hasFluidInRange(level, pos, brineFluid, NaturalTransformConfig.BRINE_EFFECT_RANGE.get());
        }

        public static boolean canBlackenNearAshWater(ServerLevel level, BlockPos pos, Block block, Fluid ashWaterFluid) {
            if (!NaturalTransformConfig.isSalinizationEnabled()) return false;
            if (ashWaterFluid == null) return false;

            // 检查是否为适合的方块类型
            if (!(isSoilBlock(block) || block == Blocks.MUD)) return false;

            // 检查灰水范围
            return hasFluidInRange(level, pos, ashWaterFluid, NaturalTransformConfig.ASH_WATER_EFFECT_RANGE.get());
        }

        public static double getBrineProbability(Block block) {
            if (block == Blocks.MUD) {
                return NaturalTransformConfig.BRINE_MUD_PROBABILITY.get();
            } else if (isSoilBlock(block)) {
                return NaturalTransformConfig.BRINE_SOIL_PROBABILITY.get();
            }
            return 0.0;
        }

        public static double getAshWaterProbability(Block block) {
            if (block == Blocks.MUD) {
                return NaturalTransformConfig.ASH_WATER_MUD_PROBABILITY.get();
            } else if (isSoilBlock(block)) {
                return NaturalTransformConfig.ASH_WATER_SOIL_PROBABILITY.get();
            }
            return 0.0;
        }

        private static boolean hasAdjacentSalineMudOnXAxis(ServerLevel level, BlockPos pos, Block salineBlock) {
            int range = NaturalTransformConfig.MANGROVE_SALINIZATION_BOOST_RANGE.get();

            for (int x = -range; x <= range; x++) {
                if (x != 0) {
                    BlockPos checkPos = pos.offset(x, 0, 0);
                    if (level.getBlockState(checkPos).getBlock() == salineBlock) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static boolean hasFluidInRange(ServerLevel level, BlockPos pos, Fluid targetFluid, int range) {
            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        BlockPos checkPos = pos.offset(x, y, z);
                        if (level.getFluidState(checkPos).getType() == targetFluid) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    /**
     * 耕地转换条件检查
     */
    public static class FarmlandTransformConditions {

        public static boolean canTransformFarmland(ServerLevel level, BlockPos pos, Block farmlandBlock) {
            if (!NaturalTransformConfig.isFarmlandTransformsEnabled()) return false;

            // 检查是否为耕地类型方块
            return isFarmlandBlock(farmlandBlock);
        }

        public static FluidEnvironment getFarmlandFluidEnvironment(ServerLevel level, BlockPos pos, Fluid brineFluid, Fluid ashWaterFluid) {
            int range = NaturalTransformConfig.FARMLAND_FLUID_CHECK_RANGE.get();

            boolean nearBrine = hasFluidInRange(level, pos, brineFluid, range);
            boolean nearAshWater = hasFluidInRange(level, pos, ashWaterFluid, range);
            boolean nearWater = hasWaterInRange(level, pos, range);

            return new FluidEnvironment(nearBrine, nearAshWater, nearWater);
        }

        public static double getFarmlandTransformProbability(Block currentFarmland, FluidEnvironment environment) {
            // 普通耕地
            if (currentFarmland == Blocks.FARMLAND) {
                if (environment.nearBrine) {
                    // 有盐水时，优先变成盐碱耕地
                    return NaturalTransformConfig.FARMLAND_TO_SALINE_FARMLAND_PROBABILITY.get();
                } else if (environment.nearAshWater) {
                    // 只有灰水时，变成黑土耕地
                    return NaturalTransformConfig.FARMLAND_TO_BLACK_FARMLAND_PROBABILITY.get();
                }
            }
            // 黑土耕地
            else if (isBlackFarmlandBlock(currentFarmland)) {
                if (environment.nearBrine) {
                    // 有盐水时，变成盐碱耕地
                    return NaturalTransformConfig.BLACK_FARMLAND_TO_SALINE_PROBABILITY.get();
                } else if (environment.nearAshWater) {
                    // 只有灰水时，增加肥力（在处理器中单独处理）
                    return NaturalTransformConfig.BLACK_FARMLAND_FERTILITY_GAIN_PROBABILITY.get();
                }
            }
            // 盐碱耕地
            else if (isSalineFarmlandBlock(currentFarmland)) {
                if (environment.nearBrine) {
                    // 有盐水时，增加盐碱化
                    return NaturalTransformConfig.SALINE_FARMLAND_SALINITY_GAIN_PROBABILITY.get();
                } else if (environment.nearWater || environment.nearAshWater) {
                    // 只有水或灰水时，降低盐碱化
                    return NaturalTransformConfig.SALINE_FARMLAND_SALINITY_LOSS_PROBABILITY.get();
                }
            }

            return 0.0;
        }

        private static boolean hasWaterInRange(ServerLevel level, BlockPos pos, int range) {
            for (int x = -range; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -range; z <= range; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        BlockPos checkPos = pos.offset(x, y, z);
                        if (level.getFluidState(checkPos).getType() == Fluids.WATER) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private static boolean hasFluidInRange(ServerLevel level, BlockPos pos, Fluid targetFluid, int range) {
            if (targetFluid == null) return false;

            for (int x = -range; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -range; z <= range; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        BlockPos checkPos = pos.offset(x, y, z);
                        if (level.getFluidState(checkPos).getType() == targetFluid) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private static boolean isFarmlandBlock(Block block) {
            return block == Blocks.FARMLAND || isBlackFarmlandBlock(block) || isSalineFarmlandBlock(block);
        }

        private static boolean isBlackFarmlandBlock(Block block) {
            String blockName = block.toString();
            return blockName.contains("black_farmland");
        }

        private static boolean isSalineFarmlandBlock(Block block) {
            String blockName = block.toString();
            return blockName.contains("saline_farmland");
        }

        public static class FluidEnvironment {
            public final boolean nearBrine;
            public final boolean nearAshWater;
            public final boolean nearWater;

            public FluidEnvironment(boolean nearBrine, boolean nearAshWater, boolean nearWater) {
                this.nearBrine = nearBrine;
                this.nearAshWater = nearAshWater;
                this.nearWater = nearWater;
            }

            public boolean canHydrate() {
                return nearBrine || nearAshWater || nearWater;
            }
        }
    }

    // ==================== 通用工具方法 ====================

    public static boolean isColdBiome(ServerLevel level, BlockPos pos) {
        try {
            Biome biome = level.getBiome(pos).value();
            return biome.coldEnoughToSnow(pos);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMangroveSwamp(ServerLevel level, BlockPos pos) {
        try {
            var biomeHolder = level.getBiome(pos);
            var biomeKey = biomeHolder.unwrapKey().orElse(null);
            if (biomeKey == null) return false;
            return biomeKey.location().getPath().equals("mangrove_swamp");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isIceBlock(Block block) {
        return block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE;
    }

    public static boolean isSoilBlock(Block block) {
        return block.defaultBlockState().is(SOIL_BLOCKS);
    }
}