package com.adonis.content.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

public class IndustrialComposterBlock extends ComposterBlock implements IWrenchable {

    public IndustrialComposterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 非潜行时，手持可堆肥物品，执行原版堆肥逻辑
        ItemStack heldItem = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() && !heldItem.isEmpty() && ComposterBlock.COMPOSTABLES.containsKey(heldItem.getItem())) {
            return super.use(state, level, pos, player, hand, hit); // 调用原版堆肥逻辑
        }

        // 潜行时，强制执行批量堆肥
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                // 在服务器端执行批量堆肥逻辑
                bulkCompost(state, level, pos, player, hand, hit);
            }
            // 客户端返回 SUCCESS 触发挥手动作，服务器返回 CONSUME 表示已处理
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // 其他情况，不触发交互
        return InteractionResult.PASS;
    }

    private InteractionResult bulkCompost(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int currentLevel = state.getValue(LEVEL);

        // 收集玩家背包中所有可堆肥的非食物物品
        List<Integer> compostableSlots = new ArrayList<>();

        // 检查背包中的物品
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;

            // 检查物品是否可堆肥且不是食物
            float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);
            if (chance > 0.0F && !stack.getItem().isEdible()) {
                compostableSlots.add(i);
            }
        }

        // 如果没有找到可堆肥物品
        if (compostableSlots.isEmpty()) {
            // 如果已满级，自动弹出骨粉
            if (currentLevel == 8) {
                return extractCompost(state, level, pos, player);
            }

            // 没有可堆肥物品时，不播放失败音效，与原版保持一致
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // 开始批量堆肥
        boolean anySuccess = false;
        RandomSource random = level.getRandom();
        boolean wasFullAfterOperation = false;

        // 处理背包中的物品
        for (int slot : compostableSlots) {
            if (currentLevel >= 8) break; // 如果已满，停止处理

            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty()) continue;

            // 获取堆肥成功率
            float chance = ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);

            // 尝试堆肥每个物品
            while (currentLevel < 8 && !stack.isEmpty()) {
                if (random.nextFloat() < chance) {
                    // 堆肥成功
                    currentLevel++;
                    level.levelEvent(1500, pos, 1);
                    level.setBlock(pos, state.setValue(LEVEL, currentLevel), 3);
                    anySuccess = true;
                } else {
                    // 堆肥失败
                    level.levelEvent(1500, pos, 0);
                }

                // 减少物品数量
                stack.shrink(1);

                // 如果堆肥桶已满，标记并停止处理
                if (currentLevel >= 8) {
                    wasFullAfterOperation = true;
                    break;
                }
            }

            // 更新物品栏
            player.getInventory().setItem(slot, stack);
        }

        // 如果有任何成功的堆肥，播放声音 (使用原版的COMPOSTER_FILL音效)
        if (anySuccess) {
            level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);

            // 如果堆肥后达到满级，自动弹出骨粉
            if (wasFullAfterOperation) {
                return extractCompost(state, level, pos, player);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // 提取骨粉的方法
    private InteractionResult extractCompost(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            // 设置堆肥等级为0
            BlockState newState = state.setValue(LEVEL, 0);
            level.setBlock(pos, newState, 3);

            // 播放骨粉提取音效
            level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

            // 创建骨粉物品并弹出或添加到玩家物品栏
            ItemStack boneMeal = new ItemStack(Items.BONE_MEAL);
            if (!player.getInventory().add(boneMeal)) {
                // 如果玩家物品栏已满，弹出骨粉物品
                popResource(level, pos, boneMeal);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
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