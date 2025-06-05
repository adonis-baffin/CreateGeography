package com.adonis.content.block;

import com.adonis.utils.ChuteConnectionHelper;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class IndustrialComposterBlock extends ComposterBlock implements IWrenchable, ProperWaterloggedBlock {
    private static final Logger LOGGER = LoggerFactory.getLogger("CreateGeography-ChuteConnection");

    // 添加连接属性
    public static final BooleanProperty NORTH_CHUTE = BooleanProperty.create("north_chute");
    public static final BooleanProperty SOUTH_CHUTE = BooleanProperty.create("south_chute");
    public static final BooleanProperty EAST_CHUTE = BooleanProperty.create("east_chute");
    public static final BooleanProperty WEST_CHUTE = BooleanProperty.create("west_chute");
    public static final BooleanProperty UP_CHUTE = BooleanProperty.create("up_chute");

    public IndustrialComposterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LEVEL, 0)
                .setValue(NORTH_CHUTE, false)
                .setValue(SOUTH_CHUTE, false)
                .setValue(EAST_CHUTE, false)
                .setValue(WEST_CHUTE, false)
                .setValue(UP_CHUTE, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, NORTH_CHUTE, SOUTH_CHUTE, EAST_CHUTE, WEST_CHUTE, UP_CHUTE, WATERLOGGED);
    }

    // 使用原版堆肥桶的形状
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getInteractionShape(state, level, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getCollisionShape(state, level, pos, context);
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
                .setValue(UP_CHUTE, connections[4]);         // UP (堆肥桶没有DOWN连接)
    }

    // 检查某个方向是否有溜槽连接
    public boolean hasChuteConnection(BlockState state, Direction direction) {
        return switch (direction) {
            case NORTH -> state.getValue(NORTH_CHUTE);
            case SOUTH -> state.getValue(SOUTH_CHUTE);
            case EAST -> state.getValue(EAST_CHUTE);
            case WEST -> state.getValue(WEST_CHUTE);
            case UP -> state.getValue(UP_CHUTE);
            case DOWN -> false; // 堆肥桶底部不接受输入
        };
    }

    // 当有物品从溜槽输入时调用此方法 - 支持批量处理
    public boolean acceptItemFromChute(ItemStack stack, Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return false;

        float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);
        if (chance <= 0.0F) return false;

        int currentLevel = state.getValue(LEVEL);
        if (currentLevel >= 8) return false;

        // 高效批量处理逻辑 - 一次性处理整个堆栈
        RandomSource random = level.getRandom();
        int consumed = 0;
        int stackSize = stack.getCount();
        int maxProcess = Math.min(stackSize, 64 - currentLevel); // 最多处理到满级

        // 批量计算成功次数
        for (int i = 0; i < maxProcess && currentLevel < 8; i++) {
            if (random.nextFloat() < chance) {
                currentLevel++;
            }
            consumed++;

            // 如果满了就停止
            if (currentLevel >= 8) break;
        }

        if (consumed > 0) {
            // 更新方块状态
            level.setBlock(pos, state.setValue(LEVEL, currentLevel), 3);
            stack.shrink(consumed);

            // 播放音效和粒子效果
            level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(1500, pos, currentLevel >= 8 ? 1 : 0);

            // 如果达到满级，自动输出骨粉
            if (currentLevel >= 8) {
                extractCompost(state.setValue(LEVEL, currentLevel), level, pos, null);
            }

            return true;
        }

        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 非潜行状态，手持可堆肥物品时使用原版逻辑
        ItemStack heldItem = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() && !heldItem.isEmpty() && ComposterBlock.COMPOSTABLES.containsKey(heldItem.getItem())) {
            return super.use(state, level, pos, player, hand, hit);
        }
        // 潜行交互由事件监听处理
        return InteractionResult.PASS;
    }

    // 批量堆肥方法（由事件调用）
    public void bulkCompost(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return;

        int currentLevel = state.getValue(LEVEL);
        List<Integer> compostableSlots = new ArrayList<>();

        // 扫描背包中的可堆肥非食物物品
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);
            if (chance > 0.0F && !stack.getItem().isEdible()) {
                compostableSlots.add(i);
            }
        }

        // 如果没有可堆肥物品且堆肥桶满级，提取骨粉
        if (compostableSlots.isEmpty()) {
            if (currentLevel == 8) {
                extractCompost(state, level, pos, player);
            }
            return;
        }

        // 开始批量堆肥
        boolean anySuccess = false;
        RandomSource random = level.getRandom();
        boolean wasFullAfterOperation = false;

        for (int slot : compostableSlots) {
            if (currentLevel >= 8) break;
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty()) continue;
            float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);

            while (currentLevel < 8 && !stack.isEmpty()) {
                if (random.nextFloat() < chance) {
                    currentLevel++;
                    level.setBlock(pos, state.setValue(LEVEL, currentLevel), 3);
                    level.levelEvent(1500, pos, 1);
                    anySuccess = true;
                } else {
                    level.levelEvent(1500, pos, 0);
                }
                stack.shrink(1);
                if (currentLevel >= 8) {
                    wasFullAfterOperation = true;
                    break;
                }
            }
            player.getInventory().setItem(slot, stack);
        }

        if (anySuccess) {
            level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (wasFullAfterOperation) {
                extractCompost(state, level, pos, player);
            }
        }
    }

    // 提取骨粉的方法
    private void extractCompost(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(LEVEL, 0), 3);
            level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            ItemStack boneMeal = new ItemStack(Items.BONE_MEAL);
            if (player != null && !player.getInventory().add(boneMeal)) {
                popResource(level, pos, boneMeal);
            } else if (player == null) {
                popResource(level, pos, boneMeal);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block changedBlock, BlockPos changedPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, changedPos, isMoving);

        if (!level.isClientSide) {
            BlockPos relative = changedPos.subtract(pos);

            // 检查变化的位置是否可能影响溜槽连接
            boolean isRelevantChange = false;

            // 斜上方位置变化（水平方向的溜槽连接）
            if (relative.getY() == 1) {
                if (Math.abs(relative.getX()) == 1 && relative.getZ() == 0) isRelevantChange = true; // 东西方向
                if (Math.abs(relative.getZ()) == 1 && relative.getX() == 0) isRelevantChange = true; // 南北方向
            }
            // 直接相邻的位置变化（垂直方向的溜槽连接）
            else if (Math.abs(relative.getX()) + Math.abs(relative.getY()) + Math.abs(relative.getZ()) == 1) {
                isRelevantChange = true;
            }

            if (isRelevantChange) {
                // 延迟更新以确保方块状态已经稳定
                level.scheduleTick(pos, this, 3);
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

        // 堆肥桶不需要检查下方，因为它不支持下方连接
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(LEVEL);
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
}