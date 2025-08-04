package com.adonis.content.block;

import com.adonis.event.NaturalTransformHandler;
import com.adonis.fluid.GeographyFluids;
import com.adonis.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class SalineFarmlandBlock extends Block {
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
    public static final int MAX_MOISTURE = 7;

    public static final IntegerProperty SALINITY = IntegerProperty.create("salinity", 0, 3);
    public static final int MAX_SALINITY = 3;  // 添加这行

    public SalineFarmlandBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(MOISTURE, 0)
                .setValue(SALINITY, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MOISTURE, SALINITY);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.UP && !state.canSurvive(level, currentPos)) {
            level.scheduleTick(currentPos, this, 1);
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos.above());
        return !blockstate.isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? Blocks.DIRT.defaultBlockState()
                : super.getStateForPlacement(context);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            turnToSalineDirt(null, state, level, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int moisture = state.getValue(MOISTURE);
        int salinity = state.getValue(SALINITY);

        // 检查周围的水源情况
        boolean hasWater = hasWater(level, pos);
        boolean hasBrine = hasBrine(level, pos);
        boolean isRaining = level.isRainingAt(pos.above());

        // 湿润度更新逻辑 - 水和盐水都可以提供湿润
        if (hasWater || hasBrine || isRaining) {
            // 有水源（普通水或盐水）或下雨时，保持最大湿润度
            if (moisture < MAX_MOISTURE) {
                level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
                moisture = MAX_MOISTURE; // 更新本地变量
            }
        } else {
            // 没有水源时，湿润度逐渐降低
            if (moisture > 0) {
                int newMoisture = moisture - 1;
                level.setBlock(pos, state.setValue(MOISTURE, newMoisture), 2);
                moisture = newMoisture; // 更新本地变量
            } else {
                // 湿润度为0时，变回盐碱土
                turnToSalineDirt(null, state, level, pos);
                return;
            }
        }

        // 盐碱化程度变化逻辑
        if (hasBrine) {
            // 有盐水时，盐碱化程度逐渐增加（无论是否有普通水）
            if (salinity < 3 && random.nextFloat() < 0.15f) { // 15%概率增加盐碱化
                int newSalinity = salinity + 1;
                level.setBlock(pos, state.setValue(SALINITY, newSalinity), 2);
            }
        } else if (hasWater) {
            // 只有普通水，没有盐水时，盐碱化程度逐渐降低
            if (salinity > 0 && random.nextFloat() < 0.25f) { // 25%概率降低盐碱化
                int newSalinity = salinity - 1;
                if (newSalinity == 0) {
                    // 盐碱化程度降到0时，变成普通耕地，保持当前湿润度
                    level.setBlock(pos, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, moisture), 2);
                    return;
                } else {
                    level.setBlock(pos, state.setValue(SALINITY, newSalinity), 2);
                }
            }
        }

        // 作物生长干预逻辑
        affectCropGrowth(state, level, pos, random);
    }

    // 影响作物生长 - 只在盐碱化等级3时退化
    private void affectCropGrowth(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int salinity = state.getValue(SALINITY);

        // 只有在极度盐碱化（等级3）时才处理作物退化
        if (salinity == 3) {
            BlockState aboveState = level.getBlockState(pos.above());
            Block aboveBlock = aboveState.getBlock();

            if (aboveBlock instanceof CropBlock crop) {
                int currentAge = crop.getAge(aboveState);

                if (currentAge > 0 && random.nextFloat() < 0.15f) { // 15%概率退化
                    int newAge = Math.max(0, currentAge - 1);
                    level.setBlock(pos.above(), crop.getStateForAge(newAge), 2);

                    // 视觉效果
                    if (!level.isClientSide) {
                        level.levelEvent(2001, pos.above(), Block.getId(aboveState));
                    }
                } else if (currentAge == 0 && random.nextFloat() < 0.05f) { // 5%概率摧毁幼苗
                    level.destroyBlock(pos.above(), true);
                }
            }
        }
    }

    // 检查附近是否有普通水源 - 修复：现在盐水也可以提供湿润
    private static boolean hasWater(Level level, BlockPos pos) {
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            FluidState fluidState = level.getFluidState(nearbyPos);

            // 检查普通水
            if (fluidState.is(FluidTags.WATER)) {
                return true;
            }
        }
        return net.minecraftforge.common.FarmlandWaterManager.hasBlockWaterTicket(level, pos);
    }

    // 检查附近是否有盐水
    private static boolean hasBrine(Level level, BlockPos pos) {
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            FluidState fluidState = level.getFluidState(nearbyPos);
            // 检查是否为盐水流体（包括源方块和流动方块）
            if (fluidState.getType() == GeographyFluids.BRINE.get().getSource() ||
                    fluidState.getType() == GeographyFluids.BRINE.get().getFlowing()) {
                return true;
            }
        }
        return false;
    }

    // 新增：统一的水源检查方法，盐水也算作水源
    public static boolean hasAnyWaterSource(Level level, BlockPos pos) {
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            FluidState fluidState = level.getFluidState(nearbyPos);

            // 检查普通水
            if (fluidState.is(FluidTags.WATER)) {
                return true;
            }

            // 检查盐水
            if (fluidState.getType() == GeographyFluids.BRINE.get().getSource() ||
                    fluidState.getType() == GeographyFluids.BRINE.get().getFlowing()) {
                return true;
            }
        }
        return net.minecraftforge.common.FarmlandWaterManager.hasBlockWaterTicket(level, pos);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide && ForgeHooks.onFarmlandTrample(level, pos, BlockRegistry.SALINE_DIRT.get().defaultBlockState(), fallDistance, entity)) {
            turnToSalineDirt(entity, state, level, pos);
        }
        super.fallOn(level, state, pos, entity, fallDistance);
    }

    public static void turnToSalineDirt(Entity entity, BlockState state, Level level, BlockPos pos) {
        // 获取当前的盐碱化等级，传递给盐碱土
        int salinity = state.getValue(SALINITY);
        BlockState salineDirtState = BlockRegistry.SALINE_DIRT.get().defaultBlockState();

        if (salineDirtState.hasProperty(SALINITY)) {
            salineDirtState = salineDirtState.setValue(SALINITY, salinity);
        }

        BlockState blockstate = pushEntitiesUp(state, salineDirtState, level, pos);
        level.setBlockAndUpdate(pos, blockstate);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
    }

    // 支持种植
    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(world, pos.relative(facing));
        return plantType == PlantType.CROP || plantType == PlantType.PLAINS;
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter world, BlockPos pos) {
        // 修复：现在盐水也可以提供肥沃度，但盐碱化会降低肥沃度
        if (hasAnyWaterSource((Level) world, pos)) {
            int salinity = state.getValue(SALINITY);
            // 盐碱化等级越高，肥沃度越低
            return salinity <= 1; // 只有盐碱化等级0-1才算肥沃
        }
        return false;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }
}