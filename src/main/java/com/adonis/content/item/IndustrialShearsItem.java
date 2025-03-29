package com.adonis.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class IndustrialShearsItem extends ShearsItem {

    public IndustrialShearsItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        // 覆盖原有方法，不消耗耐久度
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 覆盖原有方法，不消耗耐久度
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.WOOL) || state.is(BlockTags.LEAVES)) {
            return 15.0F; // 提高对羊毛和树叶的挖掘速度
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        // 设置为不可损坏
        return false;
    }
}