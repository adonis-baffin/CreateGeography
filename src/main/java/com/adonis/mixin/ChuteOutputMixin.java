//package com.adonis.mixin;
//
//import com.adonis.content.block.IndustrialComposterBlock;
//import com.adonis.content.block.IndustrialFurnaceBlock;
//import com.adonis.content.block.IndustrialFurnaceBlockEntity;
//import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
//import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.ComposterBlock;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
///**
// * 调试版ChuteOutputMixin - 添加日志查看问题
// */
//@Mixin(value = ChuteBlockEntity.class, remap = false)
//public class ChuteOutputMixin {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger("ChuteOutputDebug");
//
//    @Shadow
//    public ItemStack getItem() {
//        return null;
//    }
//
//    @Shadow
//    public void setItem(ItemStack stack) {
//    }
//
//    /**
//     * 拦截handleDownwardOutput方法，处理向工业方块的输出
//     */
//    @Inject(method = "handleDownwardOutput", at = @At("HEAD"), cancellable = true)
//    private void handleIndustrialBlockOutput(boolean simulate, CallbackInfoReturnable<Boolean> cir) {
//        ChuteBlockEntity chute = (ChuteBlockEntity)(Object)this;
//
//        Level level = chute.getLevel();
//        if (level == null || level.isClientSide) return;
//
//        BlockPos chutePos = chute.getBlockPos();
//        ItemStack item = this.getItem();
//        if (item.isEmpty()) return;
//
//        BlockState chuteState = chute.getBlockState();
//        Direction facing = AbstractChuteBlock.getChuteFacing(chuteState);
//        if (facing == null) return;
//
//        // 计算目标位置
//        BlockPos targetPos = null;
//        if (facing.getAxis().isHorizontal()) {
//            targetPos = chutePos.below().relative(facing.getOpposite());
//        } else if (facing == Direction.DOWN) {
//            targetPos = chutePos.below();
//        }
//
//        if (targetPos == null) return;
//
//        BlockState targetState = level.getBlockState(targetPos);
//
//        // 处理工业熔炉
//        if (targetState.getBlock() instanceof IndustrialFurnaceBlock) {
//            LOGGER.info("检测到工业熔炉传输，物品: {}, 数量: {}, 模拟: {}",
//                    item.getItem().getDescriptionId(), item.getCount(), simulate);
//
//            BlockEntity be = level.getBlockEntity(targetPos);
//            if (be instanceof IndustrialFurnaceBlockEntity furnace) {
//                Direction fromDirection = facing.getAxis().isHorizontal() ? facing : Direction.UP;
//
//                if (simulate) {
//                    // 模拟：直接返回true，让溜槽知道可以传输
//                    cir.setReturnValue(true);
//                } else {
//                    // 实际传输：直接调用原版的容器插入逻辑
//                    int originalCount = item.getCount();
//
//                    // 使用原版的WorldlyContainer接口
//                    int[] slots = furnace.getSlotsForFace(fromDirection);
//                    boolean inserted = false;
//
//                    for (int slot : slots) {
//                        if (furnace.canPlaceItemThroughFace(slot, item, fromDirection)) {
//                            ItemStack existing = furnace.getItem(slot);
//
//                            if (existing.isEmpty()) {
//                                // 槽位为空，放入一个物品
//                                furnace.setItem(slot, new ItemStack(item.getItem(), 1));
//                                item.shrink(1);
//                                inserted = true;
//                                break;
//                            } else if (existing.is(item.getItem()) &&
//                                    existing.getCount() < existing.getMaxStackSize()) {
//                                // 可以堆叠，增加一个
//                                existing.grow(1);
//                                item.shrink(1);
//                                inserted = true;
//                                break;
//                            }
//                        }
//                    }
//
//                    if (inserted) {
//                        LOGGER.info("成功插入物品，原数量: {}, 剩余数量: {}", originalCount, item.getCount());
//                        if (item.isEmpty()) {
//                            this.setItem(ItemStack.EMPTY);
//                        }
//                        cir.setReturnValue(true);
//                    } else {
//                        LOGGER.info("插入失败");
//                        cir.setReturnValue(false);
//                    }
//                }
//                return;
//            }
//        }
//
//        // 处理工业堆肥桶
//        if (targetState.getBlock() instanceof IndustrialComposterBlock) {
//            LOGGER.info("检测到工业堆肥桶传输，物品: {}, 等级: {}, 模拟: {}",
//                    item.getItem().getDescriptionId(),
//                    targetState.getValue(ComposterBlock.LEVEL),
//                    simulate);
//
//            if (simulate) {
//                // 模拟：检查是否能接受
//                float chance = ComposterBlock.COMPOSTABLES.getOrDefault(item.getItem(), 0.0F);
//                int currentLevel = targetState.getValue(ComposterBlock.LEVEL);
//                boolean canAccept = (chance > 0.0F && currentLevel < 8);
//                LOGGER.info("模拟结果: 可堆肥={}, 当前等级={}, 能接受={}", chance > 0, currentLevel, canAccept);
//                if (canAccept) {
//                    cir.setReturnValue(true);
//                }
//            } else {
//                // 实际传输：使用原版ComposterBlock.insertItem
//                ItemStack singleItem = new ItemStack(item.getItem(), 1);
//                BlockState oldState = targetState;
//                BlockState result = ComposterBlock.insertItem(null, targetState, (ServerLevel) level, singleItem, targetPos);
//
//                LOGGER.info("堆肥结果: 旧等级={}, 新等级={}",
//                        oldState.getValue(ComposterBlock.LEVEL),
//                        result.getValue(ComposterBlock.LEVEL));
//
//                if (!result.equals(targetState)) {
//                    // 状态改变说明成功插入
//                    item.shrink(1);
//                    if (item.isEmpty()) {
//                        this.setItem(ItemStack.EMPTY);
//                    }
//                    cir.setReturnValue(true);
//                }
//            }
//            return;
//        }
//
//        // 其他方块使用原版逻辑（不拦截）
//    }
//}