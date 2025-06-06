package com.adonis.content.block;

import com.adonis.registry.BlockEntityRegistry;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

public class IndustrialFurnaceBlockEntity extends FurnaceBlockEntity {

    private static final int BASE_SPEED_MULTIPLIER = 2;
    private static final int SUPER_HEATED_MULTIPLIER = 8;

    private boolean hasBlazeHeater = false;
    private HeatLevel blazeHeatLevel = HeatLevel.NONE;

    // 缓存的物品处理器
    private LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this,
            Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    public IndustrialFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return BlockEntityRegistry.INDUSTRIAL_FURNACE.get();
    }

    @Override
    protected int getBurnDuration(ItemStack fuel) {
        return super.getBurnDuration(fuel);
    }

    // 检查下方是否有烈焰人燃烧室并获取其热度等级
    private void checkBlazeHeater(Level level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        if (belowState.getBlock() instanceof BlazeBurnerBlock) {
            hasBlazeHeater = true;
            blazeHeatLevel = belowState.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        } else {
            hasBlazeHeater = false;
            blazeHeatLevel = HeatLevel.NONE;
        }
    }

    // 获取当前的速度乘数
    private int getCurrentSpeedMultiplier() {
        if (hasBlazeHeater && blazeHeatLevel == HeatLevel.SEETHING) {
            return SUPER_HEATED_MULTIPLIER;
        }
        return BASE_SPEED_MULTIPLIER;
    }

    /**
     * 重新设计的物品接收方法 - 模拟原版的单个物品处理
     * 返回是否成功接收了物品
     */
    public boolean acceptItemFromChute(ItemStack stack, Direction fromDirection) {
        if (level == null || level.isClientSide || stack.isEmpty()) {
            return false;
        }

        // 检查方块状态是否有此方向的溜槽连接
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof IndustrialFurnaceBlock industrialFurnace)) {
            return false;
        }

        if (!industrialFurnace.hasChuteConnection(state, fromDirection)) {
            return false;
        }

        // 确定目标槽位
        int targetSlot = getTargetSlot(stack, fromDirection);
        if (targetSlot < 0) {
            return false;
        }

        // 检查是否可以插入
        if (canInsertIntoSlot(stack, targetSlot)) {
            // 实际插入一个物品
            insertSingleItemIntoSlot(stack, targetSlot);
            return true;
        }

        return false;
    }

    /**
     * 根据方向和物品类型确定目标槽位
     */
    private int getTargetSlot(ItemStack stack, Direction fromDirection) {
        if (fromDirection == Direction.UP) {
            // 顶部只能输入原料
            return 0;
        } else if (fromDirection.getAxis().isHorizontal()) {
            // 侧面：燃料优先放燃料槽，其他放原料槽
            if (AbstractFurnaceBlockEntity.isFuel(stack)) {
                return 1;
            } else {
                return 0;
            }
        }
        return -1; // 不支持的方向
    }

    /**
     * 检查是否可以向指定槽位插入物品
     */
    private boolean canInsertIntoSlot(ItemStack stack, int slot) {
        ItemStack existing = getItem(slot);

        if (existing.isEmpty()) {
            return true;
        }

        // 检查是否可以堆叠
        return ItemStack.isSameItemSameTags(existing, stack) &&
                existing.getCount() < existing.getMaxStackSize();
    }

    /**
     * 向指定槽位插入一个物品
     */
    private void insertSingleItemIntoSlot(ItemStack stack, int slot) {
        ItemStack existing = getItem(slot);

        if (existing.isEmpty()) {
            // 槽位为空，放入一个物品
            setItem(slot, new ItemStack(stack.getItem(), 1));
        } else {
            // 槽位有物品，增加一个
            existing.grow(1);
        }

        // 从输入堆栈中移除一个
        stack.shrink(1);
        setChanged();
    }

    // 创建自定义的tick方法
    public static void industrialTick(Level level, BlockPos pos, BlockState state, IndustrialFurnaceBlockEntity entity) {
        if (!level.isClientSide) {
            entity.checkBlazeHeater(level, pos);

            if (entity.hasBlazeHeater && entity.blazeHeatLevel.isAtLeast(HeatLevel.KINDLED)) {
                entity.dataAccess.set(0, 200); // litTime
                entity.dataAccess.set(1, 200); // litDuration
            }

            int speedMultiplier = entity.getCurrentSpeedMultiplier();
            int oldProgress = entity.dataAccess.get(2);
            int oldTotalTime = entity.dataAccess.get(3);

            AbstractFurnaceBlockEntity.serverTick(level, pos, state, entity);

            int newProgress = entity.dataAccess.get(2);
            int newTotalTime = entity.dataAccess.get(3);

            if (newProgress > 0 && newProgress != oldProgress) {
                if (oldTotalTime != newTotalTime) {
                    entity.dataAccess.set(3, newTotalTime / speedMultiplier);
                }

                if (entity.dataAccess.get(0) > 0) {
                    entity.dataAccess.set(2, newProgress + (speedMultiplier - 1));
                    int adjustedTotalTime = entity.dataAccess.get(3);
                    if (entity.dataAccess.get(2) >= adjustedTotalTime) {
                        entity.dataAccess.set(2, adjustedTotalTime - 1);
                    }
                }
            }
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return handlers[0].cast();
            } else if (side == Direction.UP) {
                return handlers[0].cast(); // 顶部输入原料
            } else if (side == Direction.DOWN) {
                return handlers[1].cast(); // 底部输出结果
            } else {
                return handlers[2].cast(); // 侧面输入燃料和原料
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<? extends IItemHandler> handler : handlers) {
            handler.invalidate();
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }
}