package com.adonis.content.block;

import com.adonis.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class SaltCrystalBlock extends Block {
    // 和雪一样，最大层数为8
    public static final int MAX_LAYERS = 8;
    // 使用原版的LAYERS属性
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    // 定义每一层的高度对应的碰撞箱，和雪完全一致
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

    public SaltCrystalBlock(BlockBehaviour.Properties pProperties) {
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

    // 放置逻辑：当玩家右键点击时
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos());
        // 如果点击的是一个已有的盐晶方块
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
        // 如果手持盐晶物品且当前层数小于8，则可以叠加
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
        // 碰撞箱比视觉模型要矮一点，这样可以“陷进去”一点点
        return SHAPE_BY_LAYER[pState.getValue(LAYERS) - 1];
    }
    
    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true; // 允许方块遮挡光线
    }
    
    // 我们不需要像雪一样有复杂的生存条件，所以可以简化或移除canSurvive和updateShape

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

    // --- 自定义掉落物 ---
    
//    @Override
//    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
//        // 如果是服务器端且玩家不是创造模式
//        if (!pLevel.isClientSide && !pPlayer.isCreative()) {
//            // 根据层数掉落对应数量的盐
//            int layers = pState.getValue(LAYERS);
//            // 满方块时，如果你希望它掉落盐块而不是8个盐，可以在这里加判断
//            if (layers == MAX_LAYERS) {
//                // 这里可以掉落盐块，或者按你的设计掉落8个盐
//                // 掉落8个盐的逻辑：
//                Block.popResource(pLevel, pPos, new ItemStack(ItemRegistry.SALT.get(), layers));
//            } else {
//                Block.popResource(pLevel, pPos, new ItemStack(ItemRegistry.SALT.get(), layers));
//            }
//        }
//
//        // 调用父类方法，确保其他逻辑（如成就触发）正常运行
//        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
//    }

    // 寻路相关，直接从雪复制即可
    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        if (pType == PathComputationType.LAND) {
            // 如果层数小于5，生物可以走上去
            return pState.getValue(LAYERS) < 5;
        }
        return false;
    }
}