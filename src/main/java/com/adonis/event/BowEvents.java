//package com.adonis.event;
//
//import com.adonis.registry.ItemRegistry;
//import com.adonis.CreateGeography;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.entity.projectile.AbstractArrow;
//import net.minecraft.world.item.ArrowItem;
//import net.minecraft.world.item.BowItem;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//@Mod.EventBusSubscriber(modid = "creategeography", bus = Mod.EventBusSubscriber.Bus.FORGE)
//public class BowEvents {
//
//    @SubscribeEvent
//    public static void onBowUse(LivingEntityUseItemEvent.Finish event) {
//        ItemStack stack = event.getItem();
//
//        // 只处理弓的使用
//        if (!(stack.getItem() instanceof BowItem)) {
//            return;
//        }
//
//        // 只处理玩家实体
//        LivingEntity entity = event.getEntity();
//        if (!(entity instanceof Player)) {
//            return;
//        }
//
//        Player player = (Player) entity;
//
//        // 计算弓的使用时间 (与弓的力量相关)
//        int useTime = event.getDuration();
//        float power = BowItem.getPowerForTime(useTime);
//
//        // 如果没有足够的力，不做处理
//        if (power < 0.1F) {
//            return;
//        }
//
//        // 查找玩家背包中的爆炸箭
//        boolean infinity = player.getAbilities().instabuild ||
//                (stack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.INFINITY_ARROWS) > 0);
//
//        ItemStack arrowStack = findAmmo(player);
//
//        // 如果找到爆炸箭，处理发射逻辑
//        if (!arrowStack.isEmpty() && arrowStack.getItem() == ItemRegistry.EXPLOSIVE_ARROW.get()) {
//            boolean shouldConsume = !infinity && !player.getAbilities().instabuild;
//
//            if (!player.level().isClientSide) {
//                // 创建并设置爆炸箭实体
//                ArrowItem arrowItem = (ArrowItem)arrowStack.getItem();
//                AbstractArrow arrow = arrowItem.createArrow(player.level(), arrowStack, player);
//
//                // 设置箭的速度和随机性
//                arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
//
//                // 如果使用满力，增加伤害
//                if (power >= 1.0F) {
//                    arrow.setCritArrow(true);
//                }
//
//                // 应用附魔
//                int powerEnchant = stack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.POWER_ARROWS);
//                if (powerEnchant > 0) {
//                    arrow.setBaseDamage(arrow.getBaseDamage() + (double)powerEnchant * 0.5D + 0.5D);
//                }
//
//                int punchEnchant = stack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.PUNCH_ARROWS);
//                if (punchEnchant > 0) {
//                    arrow.setKnockback(punchEnchant);
//                }
//
//                if (stack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.FLAMING_ARROWS) > 0) {
//                    arrow.setSecondsOnFire(100);
//                }
//
//                // 添加到世界中
//                player.level().addFreshEntity(arrow);
//
//                // 播放声音
//                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
//                        SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
//                        1.0F, 1.0F / (player.level().getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
//
//                // 消耗物品
//                if (shouldConsume) {
//                    arrowStack.shrink(1);
//                    if (arrowStack.isEmpty()) {
//                        player.getInventory().removeItem(arrowStack);
//                    }
//                }
//            }
//        }
//    }
//
//    // 查找玩家可用的箭矢
//    private static ItemStack findAmmo(Player player) {
//        // 检查副手
//        if (isArrow(player.getItemInHand(net.minecraft.world.InteractionHand.OFF_HAND))) {
//            return player.getItemInHand(net.minecraft.world.InteractionHand.OFF_HAND);
//        }
//        // 检查主手
//        else if (isArrow(player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND))) {
//            return player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
//        }
//        // 检查背包
//        else {
//            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
//                ItemStack itemstack = player.getInventory().getItem(i);
//                if (isArrow(itemstack)) {
//                    return itemstack;
//                }
//            }
//            return ItemStack.EMPTY;
//        }
//    }
//
//    private static boolean isArrow(ItemStack stack) {
//        return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
//    }
//}