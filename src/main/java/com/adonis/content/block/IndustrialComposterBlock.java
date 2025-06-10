//package com.adonis.content.block;
//
//import com.adonis.utils.ChuteConnectionHelper;
//import com.simibubi.create.content.equipment.wrench.IWrenchable;
//import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
//import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
//import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.util.RandomSource;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.context.BlockPlaceContext;
//import net.minecraft.world.item.context.UseOnContext;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.LevelAccessor;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.ComposterBlock;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateDefinition;
//import net.minecraft.world.level.block.state.properties.BooleanProperty;
//import net.minecraft.world.level.material.FluidState;
//import net.minecraft.world.phys.BlockHitResult;
//import net.minecraft.world.phys.shapes.CollisionContext;
//import net.minecraft.world.phys.shapes.VoxelShape;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class IndustrialComposterBlock extends ComposterBlock implements IWrenchable, ProperWaterloggedBlock {
//
//    // 连接属性
//    public static final BooleanProperty NORTH_CHUTE = BooleanProperty.create("north_chute");
//    public static final BooleanProperty SOUTH_CHUTE = BooleanProperty.create("south_chute");
//    public static final BooleanProperty EAST_CHUTE = BooleanProperty.create("east_chute");
//    public static final BooleanProperty WEST_CHUTE = BooleanProperty.create("west_chute");
//    public static final BooleanProperty UP_CHUTE = BooleanProperty.create("up_chute");
//    public static final BooleanProperty DOWN_CHUTE = BooleanProperty.create("down_chute");
//
//    public IndustrialComposterBlock(Properties properties) {
//        super(properties);
//        this.registerDefaultState(this.stateDefinition.any()
//                .setValue(LEVEL, 0)
//                .setValue(NORTH_CHUTE, false)
//                .setValue(SOUTH_CHUTE, false)
//                .setValue(EAST_CHUTE, false)
//                .setValue(WEST_CHUTE, false)
//                .setValue(UP_CHUTE, false)
//                .setValue(DOWN_CHUTE, false)
//                .setValue(WATERLOGGED, false));
//    }
//
//    @Override
//    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
//        builder.add(LEVEL, NORTH_CHUTE, SOUTH_CHUTE, EAST_CHUTE, WEST_CHUTE, UP_CHUTE, DOWN_CHUTE, WATERLOGGED);
//    }
//
//    @Override
//    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
//        return super.getShape(state, level, pos, context);
//    }
//
//    @Override
//    public BlockState getStateForPlacement(BlockPlaceContext context) {
//        BlockState state = this.withWater(super.getStateForPlacement(context), context);
//        return updateChuteConnections(state, context.getLevel(), context.getClickedPos());
//    }
//
//    @Override
//    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
//        this.updateWater(level, state, pos);
//        BlockState updatedState = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
//        return updateChuteConnections(updatedState, level, pos);
//    }
//
//    @Override
//    public FluidState getFluidState(BlockState state) {
//        return this.fluidState(state);
//    }
//
//    // 更新溜槽连接状态
//    private BlockState updateChuteConnections(BlockState state, BlockGetter level, BlockPos pos) {
//        boolean[] connections = ChuteConnectionHelper.checkAllDirections(level, pos);
//        return state.setValue(NORTH_CHUTE, connections[0])
//                .setValue(SOUTH_CHUTE, connections[1])
//                .setValue(EAST_CHUTE, connections[2])
//                .setValue(WEST_CHUTE, connections[3])
//                .setValue(UP_CHUTE, connections[4])
//                .setValue(DOWN_CHUTE, connections[5]);
//    }
//
//    // 检查某个方向是否有溜槽连接
//    public boolean hasChuteConnection(BlockState state, Direction direction) {
//        return switch (direction) {
//            case NORTH -> state.getValue(NORTH_CHUTE);
//            case SOUTH -> state.getValue(SOUTH_CHUTE);
//            case EAST -> state.getValue(EAST_CHUTE);
//            case WEST -> state.getValue(WEST_CHUTE);
//            case UP -> state.getValue(UP_CHUTE);
//            case DOWN -> state.getValue(DOWN_CHUTE);
//        };
//    }
//
//    /**
//     * 从溜槽接受单个物品输入 - 完全重写，解决自动化问题
//     */
//    public boolean acceptItemFromChute(ItemStack stack, Level level, BlockPos pos, BlockState state) {
//        if (level.isClientSide || stack.isEmpty()) {
//            return false;
//        }
//
//        // 检查物品是否可堆肥
//        float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);
//        if (chance <= 0.0F) {
//            return false;
//        }
//
//        int currentLevel = state.getValue(LEVEL);
//
//        // 关键修复：如果满级，先尝试输出
//        if (currentLevel >= 8) {
//            if (hasChuteConnection(state, Direction.DOWN)) {
//                boolean outputSuccess = tryOutputToChute(level, pos, state);
//                if (outputSuccess) {
//                    // 成功输出后，重新获取等级
//                    currentLevel = level.getBlockState(pos).getValue(LEVEL);
//                } else {
//                    // 输出失败，拒绝接受新物品
//                    return false;
//                }
//            } else {
//                // 没有输出溜槽且满级，拒绝输入
//                return false;
//            }
//        }
//
//        // 检查等级（输出后可能已经变化）
//        if (currentLevel >= 8) {
//            return false;
//        }
//
//        // 进行堆肥判定
//        RandomSource random = level.getRandom();
//        boolean success = random.nextFloat() < chance;
//
//        if (success) {
//            currentLevel++;
//            // 更新方块状态
//            BlockState newState = state.setValue(LEVEL, currentLevel);
//            level.setBlock(pos, newState, 3);
//
//            // 播放成功音效
//            level.playSound(null, pos, SoundEvents.COMPOSTER_FILL_SUCCESS, SoundSource.BLOCKS, 1.0F, 1.0F);
//            level.levelEvent(1500, pos, 1);
//
//            // 如果满级，安排输出检查
//            if (currentLevel >= 8) {
//                level.scheduleTick(pos, this, 2);
//            }
//        } else {
//            // 堆肥失败，播放普通音效
//            level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
//            level.levelEvent(1500, pos, 0);
//        }
//
//        // 无论成功失败都消耗物品
//        stack.shrink(1);
//        return true;
//    }
//
//    /**
//     * 尝试输出骨粉到下方溜槽
//     */
//    private boolean tryOutputToChute(Level level, BlockPos pos, BlockState state) {
//        if (state.getValue(LEVEL) != 8) {
//            return false;
//        }
//
//        BlockPos chutePos = pos.below();
//        BlockState chuteState = level.getBlockState(chutePos);
//
//        if (!AbstractChuteBlock.isChute(chuteState)) {
//            return false;
//        }
//
//        BlockEntity be = level.getBlockEntity(chutePos);
//        if (!(be instanceof ChuteBlockEntity chuteEntity)) {
//            return false;
//        }
//
//        ItemStack existingItem = chuteEntity.getItem();
//
//        // 检查溜槽是否能接受骨粉
//        boolean canInsert = false;
//        if (existingItem.isEmpty()) {
//            canInsert = true;
//        } else if (existingItem.is(Items.BONE_MEAL) &&
//                existingItem.getCount() < existingItem.getMaxStackSize()) {
//            canInsert = true;
//        }
//
//        if (canInsert) {
//            // 输出到溜槽
//            if (existingItem.isEmpty()) {
//                chuteEntity.setItem(new ItemStack(Items.BONE_MEAL, 1));
//            } else {
//                existingItem.grow(1);
//            }
//
//            // 重置堆肥桶等级
//            level.setBlock(pos, state.setValue(LEVEL, 0), 3);
//            level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
//
//            return true;
//        }
//
//        return false;
//    }
//
//    /**
//     * 重写tick方法 - 处理自动输出
//     */
//    @Override
//    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
//        int currentLevel = state.getValue(LEVEL);
//
//        if (currentLevel == 7) {
//            // 原版逻辑：7级自动变8级
//            level.setBlock(pos, state.setValue(LEVEL, 8), 3);
//            level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
//
//            // 立即尝试输出
//            level.scheduleTick(pos, this, 1);
//        } else if (currentLevel == 8 && hasChuteConnection(state, Direction.DOWN)) {
//            // 8级且有下方溜槽，尝试输出
//            tryOutputToChute(level, pos, state);
//        }
//    }
//
//    /**
//     * 重写onPlace确保7级时正确调度tick
//     */
//    @Override
//    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
//        if (state.getValue(LEVEL) == 7) {
//            level.scheduleTick(pos, this, 20);
//        }
//    }
//
//    @Override
//    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
//        ItemStack heldItem = player.getItemInHand(hand);
//        if (!player.isShiftKeyDown() && !heldItem.isEmpty() && ComposterBlock.COMPOSTABLES.containsKey(heldItem.getItem())) {
//            return super.use(state, level, pos, player, hand, hit);
//        }
//        return InteractionResult.PASS;
//    }
//
//    /**
//     * 批量堆肥方法（由事件调用）
//     */
//    public void bulkCompost(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
//        if (level.isClientSide) return;
//
//        int currentLevel = state.getValue(LEVEL);
//
//        // 如果满级，尝试提取或自动输出
//        if (currentLevel == 8) {
//            if (hasChuteConnection(state, Direction.DOWN)) {
//                tryOutputToChute(level, pos, state);
//            } else {
//                extractCompost(state, level, pos, player);
//            }
//            return;
//        }
//
//        List<Integer> compostableSlots = new ArrayList<>();
//
//        // 扫描背包中的可堆肥非食物物品
//        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
//            ItemStack stack = player.getInventory().getItem(i);
//            if (stack.isEmpty()) continue;
//            float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);
//            if (chance > 0.0F && !stack.getItem().isEdible()) {
//                compostableSlots.add(i);
//            }
//        }
//
//        if (compostableSlots.isEmpty()) return;
//
//        // 批量堆肥
//        boolean anySuccess = false;
//        RandomSource random = level.getRandom();
//
//        for (int slot : compostableSlots) {
//            if (currentLevel >= 7) break; // 最多到7级，让tick处理7->8的转换
//            ItemStack stack = player.getInventory().getItem(slot);
//            if (stack.isEmpty()) continue;
//            float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);
//
//            while (currentLevel < 7 && !stack.isEmpty()) {
//                if (random.nextFloat() < chance) {
//                    currentLevel++;
//                    anySuccess = true;
//                }
//                stack.shrink(1);
//                level.levelEvent(1500, pos, currentLevel >= 7 ? 1 : 0);
//                if (currentLevel >= 7) break;
//            }
//            player.getInventory().setItem(slot, stack);
//        }
//
//        if (anySuccess) {
//            level.setBlock(pos, state.setValue(LEVEL, currentLevel), 3);
//            level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
//
//            // 如果达到7级，安排tick处理7->8转换
//            if (currentLevel == 7) {
//                level.scheduleTick(pos, this, 20);
//            }
//        }
//    }
//
//    /**
//     * 手动提取骨粉
//     */
//    private void extractCompost(BlockState state, Level level, BlockPos pos, Player player) {
//        if (!level.isClientSide && state.getValue(LEVEL) == 8) {
//            level.setBlock(pos, state.setValue(LEVEL, 0), 3);
//            level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
//            ItemStack boneMeal = new ItemStack(Items.BONE_MEAL);
//            if (player != null && !player.getInventory().add(boneMeal)) {
//                popResource(level, pos, boneMeal);
//            } else if (player == null) {
//                popResource(level, pos, boneMeal);
//            }
//        }
//    }
//
//    @Override
//    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block changedBlock, BlockPos changedPos, boolean isMoving) {
//        super.neighborChanged(state, level, pos, changedBlock, changedPos, isMoving);
//        if (!level.isClientSide) {
//            BlockState newState = updateChuteConnections(state, level, pos);
//            if (!state.equals(newState)) {
//                level.setBlockAndUpdate(pos, newState);
//            }
//        }
//    }
//
//    @Override
//    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
//        super.onRemove(state, level, pos, newState, isMoving);
//        if (!state.is(newState.getBlock()) && !level.isClientSide) {
//            updateConnectedChutes(level, pos);
//        }
//    }
//
//    private void updateConnectedChutes(Level level, BlockPos pos) {
//        Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
//
//        for (Direction direction : horizontalDirections) {
//            BlockPos chutePos = pos.above().relative(direction);
//            BlockState chuteState = level.getBlockState(chutePos);
//            if (AbstractChuteBlock.isChute(chuteState)) {
//                level.scheduleTick(chutePos, chuteState.getBlock(), 1);
//            }
//        }
//
//        BlockPos upChutePos = pos.above();
//        BlockState upChuteState = level.getBlockState(upChutePos);
//        if (AbstractChuteBlock.isChute(upChuteState)) {
//            level.scheduleTick(upChutePos, upChuteState.getBlock(), 1);
//        }
//
//        BlockPos downChutePos = pos.below();
//        BlockState downChuteState = level.getBlockState(downChutePos);
//        if (AbstractChuteBlock.isChute(downChuteState)) {
//            level.scheduleTick(downChutePos, downChuteState.getBlock(), 1);
//        }
//    }
//
//    @Override
//    public boolean hasAnalogOutputSignal(BlockState state) {
//        return true;
//    }
//
//    @Override
//    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
//        return state.getValue(LEVEL);
//    }
//
//    @Override
//    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
//        Level level = context.getLevel();
//        BlockPos pos = context.getClickedPos();
//        Player player = context.getPlayer();
//
//        if (level.isClientSide) {
//            return InteractionResult.SUCCESS;
//        }
//
//        if (player != null && player.isShiftKeyDown()) {
//            ItemStack stack = new ItemStack(this.asItem());
//            if (!stack.isEmpty()) {
//                if (player.getInventory().add(stack)) {
//                    level.removeBlock(pos, false);
//                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
//                } else {
//                    popResource(level, pos, stack);
//                    level.removeBlock(pos, false);
//                }
//                player.getInventory().setChanged();
//                return InteractionResult.SUCCESS;
//            }
//        }
//        return IWrenchable.super.onWrenched(state, context);
//    }
//}