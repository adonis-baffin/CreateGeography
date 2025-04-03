package com.adonis.content.block;

import com.adonis.registry.BlockEntityRegistry;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class IndustrialFurnaceBlockEntity extends FurnaceBlockEntity {
    // 基础加速系数 - 使处理速度加倍
    private static final int BASE_SPEED_MULTIPLIER = 2;
    // 超级加热时的加速系数
    private static final int SUPER_HEATED_MULTIPLIER = 8;

    // 记录下方是否有烈焰人燃烧室
    private boolean hasBlazeHeater = false;
    // 记录下方烈焰人燃烧室的热度等级
    private HeatLevel blazeHeatLevel = HeatLevel.NONE;

    public IndustrialFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return BlockEntityRegistry.INDUSTRIAL_FURNACE.get();
    }

    // 重写燃料燃烧时间，使其燃烧更久（保持相同的效率）
    @Override
    protected int getBurnDuration(ItemStack fuel) {
        // 我们不改变燃烧时间，因为这可能会导致燃料过快消耗
        return super.getBurnDuration(fuel);
    }

    // 检查下方是否有烈焰人燃烧室并获取其热度等级
    private void checkBlazeHeater(Level level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        // 检查下方方块是否为烈焰人燃烧室
        if (belowState.getBlock() instanceof BlazeBurnerBlock) {
            hasBlazeHeater = true;
            // 获取热度等级
            blazeHeatLevel = belowState.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        } else {
            hasBlazeHeater = false;
            blazeHeatLevel = HeatLevel.NONE;
        }
    }

    // 获取当前的速度乘数
    private int getCurrentSpeedMultiplier() {
        // 如果有烈焰人燃烧室且处于超级加热状态
        if (hasBlazeHeater && blazeHeatLevel == HeatLevel.SEETHING) {
            return SUPER_HEATED_MULTIPLIER;
        }
        // 否则使用基础加速
        return BASE_SPEED_MULTIPLIER;
    }

    // 创建自定义的tick方法
    public static void industrialTick(Level level, BlockPos pos, BlockState state, IndustrialFurnaceBlockEntity entity) {
        // 在服务器上执行原始tick
        if (!level.isClientSide) {
            // 检查下方是否有烈焰人燃烧室
            entity.checkBlazeHeater(level, pos);

            // 只有当烈焰人燃烧室处于活跃燃烧状态时才提供无限燃料
            // SMOULDERING, FADING, KINDLED, SEETHING 都是燃烧状态
            if (entity.hasBlazeHeater && entity.blazeHeatLevel.isAtLeast(HeatLevel.KINDLED)) {
                // 设置燃烧时间为最大值，确保一直保持点燃状态
                entity.dataAccess.set(0, 200); // litTime
                entity.dataAccess.set(1, 200); // litDuration
            }

            // 获取当前的速度乘数
            int speedMultiplier = entity.getCurrentSpeedMultiplier();

            // 先保存当前进度和总时间
            int oldProgress = entity.dataAccess.get(2);
            int oldTotalTime = entity.dataAccess.get(3);

            // 执行原始的tick逻辑
            AbstractFurnaceBlockEntity.serverTick(level, pos, state, entity);

            // 获取更新后的进度
            int newProgress = entity.dataAccess.get(2);
            int newTotalTime = entity.dataAccess.get(3);

            // 如果进度发生变化（正在烹饪），应用我们的加速
            if (newProgress > 0 && newProgress != oldProgress) {
                // 如果总时间改变（新物品放入），则减少总烹饪时间
                if (oldTotalTime != newTotalTime) {
                    // 将总烹饪时间减少到原来的1/speedMultiplier
                    entity.dataAccess.set(3, newTotalTime / speedMultiplier);
                }

                // 如果处于点燃状态，额外增加进度
                if (entity.dataAccess.get(0) > 0) { // litTime > 0
                    // 额外增加 (speedMultiplier-1) 个进度单位
                    entity.dataAccess.set(2, newProgress + (speedMultiplier - 1));

                    // 确保不超过总烹饪时间
                    int adjustedTotalTime = entity.dataAccess.get(3);
                    if (entity.dataAccess.get(2) >= adjustedTotalTime) {
                        entity.dataAccess.set(2, adjustedTotalTime - 1);
                    }
                }
            }
        }
    }

    // 确保NBT数据正确保存和加载
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }
}