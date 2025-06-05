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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class IndustrialFurnaceBlockEntity extends FurnaceBlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger("CreateGeography-IndustrialFurnace");

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
     * 改进的物品接收方法，支持来自溜槽的物品输入
     * 修复：防止物品重复，正确处理物品数量
     * @param stack 要输入的物品堆栈（这个堆栈会被直接修改）
     * @param fromDirection 物品来源方向
     * @return 是否成功接收物品
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
            LOGGER.debug("此方向没有溜槽连接: {}", fromDirection);
            return false;
        }

        LOGGER.debug("工业熔炉接收物品: {} 从方向 {}", stack, fromDirection);

        // 分别处理不同方向的输入
        boolean success = false;
        int originalCount = stack.getCount();

        if (fromDirection.getAxis().isHorizontal()) {
            // 侧面输入：智能分配到燃料槽或原料槽
            success = handleSideInput(stack);
        } else if (fromDirection == Direction.UP) {
            // 顶部输入：只能放入原料槽
            success = handleTopInput(stack);
        } else if (fromDirection == Direction.DOWN) {
            // 底部输入：目前不支持
            return false;
        }

        if (success) {
            setChanged();
            int consumedCount = originalCount - stack.getCount();
            LOGGER.debug("物品成功输入到工业熔炉，消耗数量: {}", consumedCount);
        }

        return success;
    }

    /**
     * 处理侧面输入 - 智能分配到燃料槽或原料槽
     */
    private boolean handleSideInput(ItemStack stack) {
        // 如果是燃料，优先尝试放入燃料槽
        if (AbstractFurnaceBlockEntity.isFuel(stack)) {
            if (tryInsertToSlot(stack, 1)) { // 燃料槽
                return true;
            }
        }

        // 否则尝试放入原料槽
        return tryInsertToSlot(stack, 0); // 原料槽
    }

    /**
     * 处理顶部输入 - 只能放入原料槽
     */
    private boolean handleTopInput(ItemStack stack) {
        return tryInsertToSlot(stack, 0); // 原料槽
    }

    /**
     * 尝试将物品插入指定槽位
     * 修复：直接修改传入的ItemStack，而不是创建新的
     * @param stack 要插入的物品（会被直接修改）
     * @param slot 目标槽位（0=原料，1=燃料，2=输出）
     * @return 是否成功插入
     */
    private boolean tryInsertToSlot(ItemStack stack, int slot) {
        ItemStack currentStack = getItem(slot);

        // 检查槽位是否为空或物品类型匹配
        if (currentStack.isEmpty()) {
            // 槽位为空，尝试插入尽可能多的物品
            int maxInsert = Math.min(stack.getCount(), stack.getMaxStackSize());
            if (maxInsert > 0) {
                setItem(slot, new ItemStack(stack.getItem(), maxInsert));
                stack.shrink(maxInsert);
                return true;
            }
        } else if (currentStack.is(stack.getItem()) && currentStack.getCount() < currentStack.getMaxStackSize()) {
            // 槽位有相同物品且未满，尝试合并
            int canAdd = currentStack.getMaxStackSize() - currentStack.getCount();
            int toAdd = Math.min(stack.getCount(), canAdd);

            if (toAdd > 0) {
                currentStack.grow(toAdd);
                stack.shrink(toAdd);
                return true;
            }
        }

        return false;
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