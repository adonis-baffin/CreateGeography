package com.adonis.mixin;

import com.adonis.content.block.IndustrialComposterBlock;
import com.adonis.content.block.IndustrialFurnaceBlock;
import com.adonis.content.block.IndustrialFurnaceBlockEntity;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复的ChuteBlockEntity Mixin - 正确处理向工业方块的物品传输
 */
@Mixin(value = ChuteBlockEntity.class, remap = false)
public class ChuteBlockEntityMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger("ChuteItemTransport");

    @Shadow
    public ItemStack getItem() {
        return null;
    }

    @Shadow
    public void setItem(ItemStack stack) {
    }

    /**
     * 拦截handleDownwardOutput方法，处理向工业方块的物品传输
     * 修复：正确计算工业方块位置，防止物品重复
     */
    @Inject(method = "handleDownwardOutput", at = @At("HEAD"), cancellable = true)
    private void handleIndustrialBlockInput(boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        ChuteBlockEntity chute = (ChuteBlockEntity)(Object)this;

        Level level = chute.getLevel();
        if (level == null || level.isClientSide) return;

        BlockPos chutePos = chute.getBlockPos();
        ItemStack item = this.getItem();

        if (item.isEmpty()) return;

        BlockState chuteState = chute.getBlockState();
        Direction facing = AbstractChuteBlock.getChuteFacing(chuteState);
        if (facing == null) return;

        LOGGER.debug("溜槽物品传输检查: 位置={}, 朝向={}", chutePos, facing);

        // 关键修复：根据溜槽朝向正确计算工业方块位置
        BlockPos industrialBlockPos = null;

        if (facing.getAxis().isHorizontal()) {
            // 水平朝向的溜槽：工业方块在溜槽的斜下方
            // 例如：溜槽朝向SOUTH，工业方块在溜槽的下方+北方（opposite）
            industrialBlockPos = chutePos.below().relative(facing.getOpposite());
            LOGGER.debug("水平朝向溜槽，计算工业方块位置: {}", industrialBlockPos);
        } else if (facing == Direction.DOWN) {
            // 向下朝向的溜槽：工业方块就在溜槽下方
            industrialBlockPos = chutePos.below();
            LOGGER.debug("向下朝向溜槽，计算工业方块位置: {}", industrialBlockPos);
        }

        if (industrialBlockPos == null) {
            LOGGER.debug("无法确定工业方块位置");
            return;
        }

        BlockState industrialState = level.getBlockState(industrialBlockPos);
        LOGGER.debug("检查位置 {} 的方块: {}", industrialBlockPos, industrialState.getBlock().getClass().getSimpleName());

        // 处理工业熔炉输入
        if (industrialState.getBlock() instanceof IndustrialFurnaceBlock) {
            LOGGER.info("发现工业熔炉，尝试输入物品: {}", item);

            BlockEntity be = level.getBlockEntity(industrialBlockPos);
            if (be instanceof IndustrialFurnaceBlockEntity furnace) {
                // 计算物品来源方向（相对于工业方块）
                Direction fromDirection = getItemSourceDirection(chutePos, industrialBlockPos, facing);
                LOGGER.debug("物品来源方向: {}", fromDirection);

                if (!simulate) {
                    // 实际传输 - 关键修复：创建副本防止原始物品被修改
                    ItemStack transferStack = item.copy();
                    int originalCount = transferStack.getCount();

                    boolean accepted = furnace.acceptItemFromChute(transferStack, fromDirection);
                    int consumedCount = originalCount - transferStack.getCount();

                    LOGGER.info("工业熔炉传输结果: 接受={}, 消耗数量={}", accepted, consumedCount);

                    if (accepted && consumedCount > 0) {
                        // 从溜槽中移除相应数量的物品
                        item.shrink(consumedCount);
                        if (item.isEmpty()) {
                            this.setItem(ItemStack.EMPTY);
                        }
                        cir.setReturnValue(true);
                        return;
                    }
                } else {
                    // 模拟传输
                    ItemStack testStack = item.copy();
                    boolean canAccept = furnace.acceptItemFromChute(testStack, fromDirection);

                    if (canAccept && testStack.getCount() < item.getCount()) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }

        // 处理工业堆肥桶输入
        if (industrialState.getBlock() instanceof IndustrialComposterBlock composter) {
            LOGGER.info("发现工业堆肥桶，尝试输入物品: {}", item);

            if (!simulate) {
                // 实际传输 - 创建副本防止重复
                ItemStack transferStack = item.copy();
                int originalCount = transferStack.getCount();

                boolean accepted = composter.acceptItemFromChute(transferStack, level, industrialBlockPos, industrialState);
                int consumedCount = originalCount - transferStack.getCount();

                LOGGER.info("工业堆肥桶传输结果: 接受={}, 消耗数量={}", accepted, consumedCount);

                if (accepted && consumedCount > 0) {
                    // 从溜槽中移除相应数量的物品
                    item.shrink(consumedCount);
                    if (item.isEmpty()) {
                        this.setItem(ItemStack.EMPTY);
                    }
                    cir.setReturnValue(true);
                    return;
                }
            } else {
                // 模拟传输
                ItemStack testStack = item.copy();
                boolean canAccept = composter.acceptItemFromChute(testStack, level, industrialBlockPos, industrialState);

                if (canAccept && testStack.getCount() < item.getCount()) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        LOGGER.debug("目标不是工业方块，继续原版逻辑");
    }

    /**
     * 计算物品来源方向（相对于工业方块）
     */
    private Direction getItemSourceDirection(BlockPos chutePos, BlockPos industrialPos, Direction chuteFacing) {
        if (chuteFacing.getAxis().isHorizontal()) {
            // 水平朝向的溜槽：物品从工业方块的对应侧面输入
            return chuteFacing;
        } else if (chuteFacing == Direction.DOWN) {
            // 向下朝向的溜槽：物品从工业方块顶部输入
            return Direction.UP;
        }
        return Direction.UP; // 默认从顶部
    }
}