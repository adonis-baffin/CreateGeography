package com.adonis.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FrozenSoilBlock extends Block {

    // 定义 SNOWY 属性，用于兼容原版草方块的雪覆盖特性
    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

    public FrozenSoilBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SNOWY, Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOWY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState stateAbove = level.getBlockState(pos.above());
        // 检查上方是否有雪
        return this.defaultBlockState().setValue(SNOWY, isSnowySetting(stateAbove));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // 如果是上方方块更新，检查是否需要更新 SNOWY 状态
        if (direction == Direction.UP) {
            return state.setValue(SNOWY, isSnowySetting(neighborState));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private static boolean isSnowySetting(BlockState state) {
        return state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.SNOW) || state.is(Blocks.POWDER_SNOW);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 检查是否应该融化
        if (shouldMelt(level, pos)) {
            melt(state, level, pos);
        }
    }

    // 检查是否满足融化条件
    private boolean shouldMelt(ServerLevel level, BlockPos pos) {
        // 检查所有面的方块光照等级
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.relative(direction);
            // 获取方块光照等级（不包括阳光）
            int blockLight = level.getBrightness(LightLayer.BLOCK, adjacentPos);
            if (blockLight > 11) {
                return true;
            }
        }
        return false;
    }

    // 融化成泥巴
    private void melt(BlockState state, ServerLevel level, BlockPos pos) {
        // 无论在哪个维度，都融化成泥巴
        // 使用标志2来避免粒子效果，就像原版冰融化一样
        level.setBlock(pos, Blocks.MUD.defaultBlockState(), 2);
        // 通知邻居方块更新
        level.neighborChanged(pos, Blocks.MUD, pos);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        // 放置时立即检查是否需要融化
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            if (shouldMelt(serverLevel, pos)) {
                // 稍后融化，避免放置时立即融化
                level.scheduleTick(pos, this, 20); // 1秒后检查
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (shouldMelt(level, pos)) {
            melt(state, level, pos);
        }
    }

    // 支持锄头转换（可选功能）
    @Override
    public BlockState getToolModifiedState(BlockState state, net.minecraft.world.item.context.UseOnContext context,
                                           net.minecraftforge.common.ToolAction toolAction, boolean simulate) {
        // 如果使用锄头，可以转换成普通泥土
        if (toolAction == net.minecraftforge.common.ToolActions.HOE_TILL) {
            return Blocks.DIRT.defaultBlockState();
        }
        return null;
    }
}