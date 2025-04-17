package com.adonis.content.item;

import com.adonis.entity.EnderCrystalArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class EnderCrystalArrowItem extends ArrowItem {
    
    public EnderCrystalArrowItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        return new EnderCrystalArrowEntity(level, shooter);
    }
    
    // 末影水晶箭不应该被无限附魔影响，因为太强大了
    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.world.entity.player.Player player) {
        return false;
    }
}