package com.adonis.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class NiterCrystalBlock extends Block {
    // 和盐晶一样，最大层数为8
    public static final int MAX_LAYERS = 8;
    // 使用原版的LAYERS属性
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    // 定义每一层的高度对应的碰撞箱，和盐晶完全一致
    protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{
            Shapes.empty(), // 0层（技术上不存在，但数组需要）
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),  // 1层
            Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),  // 2层
            Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),  // 3层
            Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),  // 4层
            Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), // 5层
            Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), // 6层
            Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), // 7层
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)  // 8层（满方块）
    };

    public NiterCrystalBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        // 注册默认方块状态，默认放置时是1层
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    // --- 核心逻辑 ---

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        // 告诉游戏这个方块有一个 "layers" 的状态属性
        pBuilder.add(LAYERS);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos());
        // 如果点击的是一个已有的硝晶方块
        if (blockState.is(this)) {
            int i = blockState.getValue(LAYERS);
            // 将层数+1，但最多不超过8
            return blockState.setValue(LAYERS, Math.min(MAX_LAYERS, i + 1));
        }
        return super.getStateForPlacement(pContext);
    }

    // 放置逻辑：判断是否可以替换现有方块
    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        int i = pState.getValue(LAYERS);
        // 如果手持硝晶物品且当前层数小于8，则可以叠加
        if (pUseContext.getItemInHand().is(this.asItem()) && i < MAX_LAYERS) {
            // 如果点击的是方块的上表面，允许叠加
            if (pUseContext.replacingClickedOnBlock()) {
                return pUseContext.getClickedFace() == Direction.UP;
            } else {
                return true;
            }
        }
        return i == 1; // 如果只有1层，可以被其他方块轻易替换掉
    }
    
    // --- 物理和视觉属性 ---

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        // 碰撞箱比视觉模型要矮一点，这样可以"陷进去"一点点
        return SHAPE_BY_LAYER[pState.getValue(LAYERS) - 1];
    }
    
    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true; // 允许方块遮挡光线
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        // 简化生存逻辑：只要下面是完整固体方块就可以
        BlockState belowState = pLevel.getBlockState(pPos.below());
        return Block.isFaceFull(belowState.getCollisionShape(pLevel, pPos.below()), Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        // 如果下面的方块没了，自己也消失
        if (!pState.canSurvive(pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        if (pType == PathComputationType.LAND) {
            // 如果层数小于5，生物可以走上去
            return pState.getValue(LAYERS) < 5;
        }
        return false;
    }
}