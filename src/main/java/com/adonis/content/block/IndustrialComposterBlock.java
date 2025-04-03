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
        if (level.isClientSide) return; // 仅在服务器端执行

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
                    level.levelEvent(1500, pos, 1); // 成功粒子效果
                    anySuccess = true;
                } else {
                    level.levelEvent(1500, pos, 0); // 失败粒子效果
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
            if (!player.getInventory().add(boneMeal)) {
                popResource(level, pos, boneMeal);
            }
        }
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