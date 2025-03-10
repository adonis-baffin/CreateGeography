package com.adonis.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;
import com.adonis.registry.BlockRegistry;

public class BlackFarmlandBlock extends FarmBlock {
    public BlackFarmlandBlock(Properties properties) {
        super(properties.randomTicks()); // 启用 randomTick
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        System.out.println("[BlackFarmland] tick called at " + pos);
        if (!state.canSurvive(level, pos)) {
            turnToBlackDirt(state, level, pos);
            return;
        }
        accelerateCropGrowth(state, level, pos, rand);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        System.out.println("[BlackFarmland] randomTick called at " + pos);
        int moisture = state.getValue(MOISTURE);
        if (!hasWater(level, pos) && !level.isRainingAt(pos.above())) {
            if (moisture > 0) {
                level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
            } else if (!hasCropAbove(level, pos)) {
                turnToBlackDirt(state, level, pos);
            }
        } else if (moisture < 7) {
            level.setBlock(pos, state.setValue(MOISTURE, 7), 2);
        }
        accelerateCropGrowth(state, level, pos, random);
    }

    private void accelerateCropGrowth(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState aboveState = level.getBlockState(pos.above());
        Block aboveBlock = aboveState.getBlock();
        int lightLevel = level.getMaxLocalRawBrightness(pos.above()); // 获取上方光照等级

        // 基础生长概率较低，且受光照影响
        float growthChance = lightLevel >= 9 ? 0.14f : 0.08f;

        if (random.nextFloat() < growthChance) {
            if (aboveBlock instanceof CropBlock crop) {
                int currentAge = crop.getAge(aboveState);
                int maxAge = crop.getMaxAge();

                if (currentAge < maxAge) {
                    if (ForgeHooks.onCropsGrowPre(level, pos.above(), aboveState, true)) {
                        int newAge = currentAge + 1; // 每次只增加一级
                        level.setBlock(pos.above(), crop.getStateForAge(newAge), 2);
                        if (!level.isClientSide) {
                            level.levelEvent(2005, pos.above(), 0);
                        }
                        ForgeHooks.onCropsGrowPost(level, pos.above(), aboveState);
                    }
                }
            } else if (aboveBlock instanceof BonemealableBlock growable) {
                if (growable.isValidBonemealTarget(level, pos.above(), aboveState, false) &&
                        ForgeHooks.onCropsGrowPre(level, pos.above(), aboveState, true)) {
                    growable.performBonemeal(level, random, pos.above(), aboveState);
                    if (!level.isClientSide) {
                        level.levelEvent(2005, pos.above(), 0);
                    }
                    ForgeHooks.onCropsGrowPost(level, pos.above(), aboveState);
                }
            }
        }
    }

    // 检查附近是否有水源
    private static boolean hasWater(Level level, BlockPos pos) {
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (level.getFluidState(nearbyPos).is(FluidTags.WATER)) {
                return true;
            }
        }
        return net.minecraftforge.common.FarmlandWaterManager.hasBlockWaterTicket(level, pos);
    }

    // 将耕地退化为黑土
    public static void turnToBlackDirt(BlockState state, Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, pushEntitiesUp(state, BlockRegistry.BLACK_FARMLAND.get().defaultBlockState(), level, pos));
    }

    // 检查上方是否有作物
    private boolean hasCropAbove(BlockGetter level, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());
        return aboveState.getBlock() instanceof CropBlock || aboveState.getBlock() instanceof StemBlock;
    }

    // 添加种植支持
    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(world, pos.above());
        return plantType == PlantType.CROP || plantType == PlantType.PLAINS;
    }

    // 确保耕地生存条件支持作物
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());
        return super.canSurvive(state, level, pos) || aboveState.getBlock() instanceof CropBlock || aboveState.getBlock() instanceof StemBlock;
    }
}