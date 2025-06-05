package com.adonis.content.block;

import com.adonis.registry.BlockEntityRegistry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import com.adonis.utils.ChuteConnectionHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndustrialFurnaceBlock extends FurnaceBlock implements IWrenchable, ProperWaterloggedBlock {
    private static final Logger LOGGER = LoggerFactory.getLogger("CreateGeography-ChuteConnection");

    // 添加连接属性，表示六个方向是否有溜槽连接
    public static final BooleanProperty NORTH_CHUTE = BooleanProperty.create("north_chute");
    public static final BooleanProperty SOUTH_CHUTE = BooleanProperty.create("south_chute");
    public static final BooleanProperty EAST_CHUTE = BooleanProperty.create("east_chute");
    public static final BooleanProperty WEST_CHUTE = BooleanProperty.create("west_chute");
    public static final BooleanProperty UP_CHUTE = BooleanProperty.create("up_chute");
    public static final BooleanProperty DOWN_CHUTE = BooleanProperty.create("down_chute");

    public IndustrialFurnaceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(NORTH_CHUTE, false)
                .setValue(SOUTH_CHUTE, false)
                .setValue(EAST_CHUTE, false)
                .setValue(WEST_CHUTE, false)
                .setValue(UP_CHUTE, false)
                .setValue(DOWN_CHUTE, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH_CHUTE, SOUTH_CHUTE, EAST_CHUTE, WEST_CHUTE, UP_CHUTE, DOWN_CHUTE, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.withWater(super.getStateForPlacement(context), context);
        return updateChuteConnections(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        this.updateWater(level, state, pos);
        BlockState updatedState = super.updateShape(state, direction, neighborState, level, pos, neighborPos);

        // 关键：每次邻居方块变化时都重新检查连接
        return updateChuteConnections(updatedState, level, pos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return this.fluidState(state);
    }

    // 更新溜槽连接状态
    private BlockState updateChuteConnections(BlockState state, BlockGetter level, BlockPos pos) {
        boolean[] connections = ChuteConnectionHelper.checkAllDirections(level, pos);

        return state.setValue(NORTH_CHUTE, connections[0])   // NORTH
                .setValue(SOUTH_CHUTE, connections[1])       // SOUTH
                .setValue(EAST_CHUTE, connections[2])        // EAST
                .setValue(WEST_CHUTE, connections[3])        // WEST
                .setValue(UP_CHUTE, connections[4])          // UP
                .setValue(DOWN_CHUTE, connections[5]);       // DOWN
    }

    // 检查某个方向是否有溜槽连接
    public boolean hasChuteConnection(BlockState state, Direction direction) {
        return switch (direction) {
            case NORTH -> state.getValue(NORTH_CHUTE);
            case SOUTH -> state.getValue(SOUTH_CHUTE);
            case EAST -> state.getValue(EAST_CHUTE);
            case WEST -> state.getValue(WEST_CHUTE);
            case UP -> state.getValue(UP_CHUTE);
            case DOWN -> state.getValue(DOWN_CHUTE);
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialFurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(
                type,
                BlockEntityRegistry.INDUSTRIAL_FURNACE.get(),
                (lvl, pos, blockState, blockEntity) -> IndustrialFurnaceBlockEntity.industrialTick(lvl, pos, blockState, (IndustrialFurnaceBlockEntity) blockEntity)
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        // 检查是否手持溜槽且满足连接条件
        if (heldItem.getItem() == AllBlocks.CHUTE.asItem()) {
            Direction face = hit.getDirection();
            // 水平面且非潜行 = 溜槽连接模式
            if (face.getAxis().isHorizontal() && !player.isShiftKeyDown()) {
                // 让事件处理器处理溜槽连接，这里不打开GUI
                return InteractionResult.PASS;
            }
        }

        // 其他情况正常打开GUI
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player != null && player.isShiftKeyDown()) {
            ItemStack stack = new ItemStack(this.asItem());
            if (!stack.isEmpty()) {
                if (player.getInventory().add(stack)) {
                    level.removeBlock(pos, false);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                    popResource(level, pos, stack);
                    level.removeBlock(pos, false);
                }
                player.getInventory().setChanged();
                return InteractionResult.SUCCESS;
            }
        }
        return IWrenchable.super.onWrenched(state, context);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block changedBlock, BlockPos changedPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, changedPos, isMoving);

        if (!level.isClientSide) {
            // 使用统一的连接检测并更新状态
            BlockState newState = updateChuteConnections(state, level, pos);
            if (!state.equals(newState)) {
                level.setBlockAndUpdate(pos, newState);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // 处理方块实体的移除
        super.onRemove(state, level, pos, newState, isMoving);

        // 如果方块真的被移除了（而不是仅仅状态改变），更新相邻溜槽
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            updateConnectedChutes(level, pos);
        }
    }

    /**
     * 更新所有连接的溜槽，让它们重新检查连接状态
     */
    private void updateConnectedChutes(Level level, BlockPos pos) {
        // 检查所有可能连接溜槽的位置
        Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction direction : horizontalDirections) {
            // 水平方向的溜槽位置（斜上方）
            BlockPos chutePos = pos.above().relative(direction);
            BlockState chuteState = level.getBlockState(chutePos);

            if (AbstractChuteBlock.isChute(chuteState)) {
                // 安排溜槽更新其状态
                level.scheduleTick(chutePos, chuteState.getBlock(), 1);
            }
        }

        // 检查正上方的溜槽
        BlockPos upChutePos = pos.above();
        BlockState upChuteState = level.getBlockState(upChutePos);
        if (AbstractChuteBlock.isChute(upChuteState)) {
            level.scheduleTick(upChutePos, upChuteState.getBlock(), 1);
        }

        // 检查正下方的溜槽
        BlockPos downChutePos = pos.below();
        BlockState downChuteState = level.getBlockState(downChutePos);
        if (AbstractChuteBlock.isChute(downChuteState)) {
            level.scheduleTick(downChutePos, downChuteState.getBlock(), 1);
        }
    }
}