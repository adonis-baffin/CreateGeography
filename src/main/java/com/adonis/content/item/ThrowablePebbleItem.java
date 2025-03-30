package com.adonis.content.item;

import com.adonis.entity.ThrownPebbleEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrowablePebbleItem extends Item {
    
    public ThrowablePebbleItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // 播放投掷音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        
        // 冷却时间
        if (!level.isClientSide) {
            // 创建投掷物实体
            ThrownPebbleEntity pebble = new ThrownPebbleEntity(level, player);
            pebble.setItem(itemstack);
            pebble.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(pebble);
        }
        
        // 增加使用统计
        player.awardStat(Stats.ITEM_USED.get(this));
        
        // 非创造模式消耗物品
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}