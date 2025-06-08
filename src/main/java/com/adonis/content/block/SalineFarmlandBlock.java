package com.adonis.content.block;

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

    // 盐碱化程度属性 (0-3, 数值越高盐碱化越严重)
    // 0: 轻度盐碱化（减慢生长50%）
    // 1: 中度盐碱化（减慢生长75%）
    // 2: 重度盐碱化（停止生长）
    // 3: 极度盐碱化（作物退化）
    public static final IntegerProperty SALINITY = IntegerProperty.create("salinity", 0, 3);

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
            turnToDirt(null, state, level, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int moisture = state.getValue(MOISTURE);
        int salinity = state.getValue(SALINITY);

        // 水分更新逻辑
        if (!hasWater(level, pos) && !level.isRainingAt(pos.above())) {
            if (moisture > 0) {
                level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
            }
        } else if (moisture < 7) {
            level.setBlock(pos, state.setValue(MOISTURE, 7), 2);
        }

        // 盐碱化加重逻辑
        if (random.nextFloat() < 0.1f && salinity < 3) { // 10%概率增加盐碱化
            level.setBlock(pos, state.setValue(SALINITY, salinity + 1), 2);
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
        // 等级0-2的生长控制由CropBlockMixin处理，避免重复
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

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide && ForgeHooks.onFarmlandTrample(level, pos, Blocks.DIRT.defaultBlockState(), fallDistance, entity)) {
            turnToDirt(entity, state, level, pos);
        }
        super.fallOn(level, state, pos, entity, fallDistance);
    }

    public static void turnToDirt(Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState blockstate = pushEntitiesUp(state, Blocks.DIRT.defaultBlockState(), level, pos);
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
        // 盐碱地不肥沃
        return false;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }
}