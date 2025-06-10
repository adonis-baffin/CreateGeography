//package com.adonis.mixin;
//
//import com.adonis.content.block.IndustrialComposterBlock;
//import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
//import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.block.ComposterBlock;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
///**
// * 安全的ComposterMixin - 不取消方法，只在完成后处理
// */
//@Mixin(ComposterBlock.class)
//public class SafeComposterFix {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger("SafeComposterFix");
//
//    /**
//     * 拦截insertItem方法完成后，不修改返回值
//     */
//    @Inject(method = "insertItem", at = @At("RETURN"))
//    private static void onInsertItemComplete(Entity entity, BlockState state, ServerLevel level, ItemStack stack, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
//        if (!(state.getBlock() instanceof IndustrialComposterBlock)) {
//            return;
//        }
//
//        BlockState result = cir.getReturnValue();
//        int newLevel = result.getValue(ComposterBlock.LEVEL);
//
//        LOGGER.info("insertItem完成: 位置={}, 新等级={}", pos, newLevel);
//
//        if (newLevel == 7) {
//            // 7级时，安排快速转换为8级
//            LOGGER.info("7级堆肥桶，安排转换为8级");
//            level.scheduleTick(pos, result.getBlock(), 1); // 1 tick后转换
//        } else if (newLevel == 8) {
//            // 8级时，立即尝试输出
//            LOGGER.info("8级堆肥桶，立即尝试输出");
//            handleLevel8Output(level, pos, result);
//        }
//    }
//
//    /**
//     * 自定义tick处理 - 拦截原版tick
//     */
//    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
//    private void customTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
//        if (!(state.getBlock() instanceof IndustrialComposterBlock)) {
//            return; // 不是工业堆肥桶，使用原版逻辑
//        }
//
//        int currentLevel = state.getValue(ComposterBlock.LEVEL);
//        LOGGER.info("自定义tick: 位置={}, 等级={}", pos, currentLevel);
//
//        if (currentLevel == 7) {
//            // 7级转8级
//            LOGGER.info("tick中7级转8级");
//            BlockState level8State = state.setValue(ComposterBlock.LEVEL, 8);
//            level.setBlock(pos, level8State, 3);
//            level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
//
//            // 立即尝试输出
//            handleLevel8Output(level, pos, level8State);
//
//            ci.cancel(); // 取消原版tick
//        } else if (currentLevel == 8) {
//            // 8级尝试输出
//            LOGGER.info("tick中8级尝试输出");
//            handleLevel8Output(level, pos, state);
//
//            ci.cancel(); // 取消原版tick
//        }
//        // 其他等级使用原版tick逻辑（不取消）
//    }
//
//    /**
//     * 处理8级输出逻辑
//     */
//    private static void handleLevel8Output(ServerLevel level, BlockPos pos, BlockState state) {
//        IndustrialComposterBlock composter = (IndustrialComposterBlock) state.getBlock();
//        if (composter.hasChuteConnection(state, Direction.DOWN)) {
//            LOGGER.info("检测到下方溜槽，尝试输出");
//            if (tryOutputBoneMeal(level, pos)) {
//                level.setBlock(pos, state.setValue(ComposterBlock.LEVEL, 0), 3);
//                LOGGER.info("输出成功，重置为0级");
//            } else {
//                // 输出失败，稍后重试
//                level.scheduleTick(pos, state.getBlock(), 20);
//                LOGGER.info("输出失败，安排重试");
//            }
//        } else {
//            LOGGER.info("没有下方溜槽连接，保持8级");
//        }
//    }
//
//    private static boolean tryOutputBoneMeal(ServerLevel level, BlockPos pos) {
//        BlockPos chutePos = pos.below();
//        BlockState chuteState = level.getBlockState(chutePos);
//
//        if (!AbstractChuteBlock.isChute(chuteState)) {
//            LOGGER.info("下方不是溜槽: {}", chuteState.getBlock());
//            return false;
//        }
//
//        BlockEntity be = level.getBlockEntity(chutePos);
//        if (!(be instanceof ChuteBlockEntity chuteEntity)) {
//            LOGGER.info("下方没有溜槽实体");
//            return false;
//        }
//
//        ItemStack existingItem = chuteEntity.getItem();
//        LOGGER.info("溜槽当前物品: {}", existingItem);
//
//        if (existingItem.isEmpty()) {
//            chuteEntity.setItem(new ItemStack(Items.BONE_MEAL, 1));
//            level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
//            LOGGER.info("成功放入骨粉到空溜槽");
//            return true;
//        } else if (existingItem.is(Items.BONE_MEAL) &&
//                  existingItem.getCount() < existingItem.getMaxStackSize()) {
//            existingItem.grow(1);
//            level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
//            LOGGER.info("成功增加骨粉到溜槽");
//            return true;
//        }
//
//        LOGGER.info("溜槽满了或物品不匹配: 物品={}, 数量={}", existingItem.getItem(), existingItem.getCount());
//        return false;
//    }
//}