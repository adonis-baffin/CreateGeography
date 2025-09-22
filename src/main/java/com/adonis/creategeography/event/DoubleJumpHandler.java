// 修改后的 DoubleJumpHandler.java
package com.adonis.creategeography.event;

import com.adonis.creategeography.content.item.TrekkingPoles;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "creategeography", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DoubleJumpHandler {
    private static final Map<Player, Boolean> CAN_DOUBLE_JUMP = new HashMap<>();
    private static final Map<Player, Boolean> HAS_DOUBLE_JUMPED = new HashMap<>();
    private static final Map<Player, Long> JUMP_START_TIME = new HashMap<>();
    private static final Map<Player, Boolean> LAST_ON_GROUND = new HashMap<>();
    private static final int JUMP_PROTECTION_TICKS = 15;

    // 服务端逻辑
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }

        boolean isHoldingPoles = player.getMainHandItem().getItem() instanceof TrekkingPoles ||
                player.getOffhandItem().getItem() instanceof TrekkingPoles;

        if (!isHoldingPoles) {
            CAN_DOUBLE_JUMP.remove(player);
            HAS_DOUBLE_JUMPED.remove(player);
            JUMP_START_TIME.remove(player);
            LAST_ON_GROUND.remove(player);
            return;
        }

        boolean wasOnGround = LAST_ON_GROUND.getOrDefault(player, true);
        boolean isOnGround = player.onGround();

        // 检测普通跳跃开始
        if (wasOnGround && !isOnGround && player.getDeltaMovement().y > 0) {
            JUMP_START_TIME.put(player, player.level().getGameTime());
        }

        // 在跳跃保护时间内清零fallDistance
        if (JUMP_START_TIME.containsKey(player) && !isOnGround) {
            long currentTime = player.level().getGameTime();
            long jumpTime = JUMP_START_TIME.get(player);

            // 如果在保护时间内（0.3秒），清零fallDistance
            if (currentTime - jumpTime <= JUMP_PROTECTION_TICKS) {
                player.fallDistance = 0.0F;
            }
        }

        // 检测刚落地的情况
        if (!wasOnGround && isOnGround) {
            handleLanding(player);
        }

        // 重置状态
        if (isOnGround) {
            CAN_DOUBLE_JUMP.put(player, true);
            // 落地后清除相关记录
            HAS_DOUBLE_JUMPED.remove(player);
            JUMP_START_TIME.remove(player);
        }

        // 更新上一tick的onGround状态
        LAST_ON_GROUND.put(player, isOnGround);
    }

    // 处理落地时的音效
    private static void handleLanding(Player player) {
        // 如果玩家血量没有减少（没受到摔落伤害），播放缓冲音效
        // 这里简单检查fallDistance是否很小，表示没有受到伤害
        if (player.fallDistance <= 3.0F || JUMP_START_TIME.containsKey(player)) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WOOL_FALL, SoundSource.PLAYERS, 0.8F, 1.3F);
        }
    }

    // 二段跳的额外摔落伤害减免
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 如果是摔落伤害且玩家曾触发过二段跳，提供额外减免
        if (event.getSource().is(DamageTypes.FALL) && HAS_DOUBLE_JUMPED.containsKey(player)) {
            boolean isHoldingPoles = player.getMainHandItem().getItem() instanceof TrekkingPoles ||
                    player.getOffhandItem().getItem() instanceof TrekkingPoles;

            if (isHoldingPoles) {
                // 二段跳额外减少2点摔落伤害
                float originalDamage = event.getAmount();
                float reducedDamage = Math.max(0, originalDamage - 2.0F);

                if (reducedDamage <= 0) {
                    // 完全取消伤害
                    event.setCanceled(true);
                    // 播放完全缓冲音效
                    if (!player.level().isClientSide) {
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.WOOL_FALL, SoundSource.PLAYERS, 1.0F, 1.5F);
                    }
                } else {
                    // 设置减免后的伤害
                    event.setAmount(reducedDamage);
                    // 播放部分缓冲音效
                    if (!player.level().isClientSide) {
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.WOOL_FALL, SoundSource.PLAYERS, 0.8F, 1.2F);
                    }
                }
            }
        }
    }

    // 客户端逻辑：检测跳跃键输入并触发二段跳
    @Mod.EventBusSubscriber(modid = "creategeography", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientHandler {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            boolean isHoldingPoles = mc.player.getMainHandItem().getItem() instanceof TrekkingPoles ||
                    mc.player.getOffhandItem().getItem() instanceof TrekkingPoles;

            if (!isHoldingPoles) return;

            // 当玩家在空中按下跳跃键时，尝试触发二段跳
            if (mc.options.keyJump.isDown() && !mc.player.onGround() &&
                    CAN_DOUBLE_JUMP.getOrDefault(mc.player, false)) {

                // 标记触发了二段跳
                HAS_DOUBLE_JUMPED.put(mc.player, true);
                // 重新记录跳跃时间
                JUMP_START_TIME.put(mc.player, mc.player.level().getGameTime());

                mc.player.jumpFromGround();
                // 默认跳跃高度0.42D，增加15%为0.483D
                mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.483D, mc.player.getDeltaMovement().z);
                CAN_DOUBLE_JUMP.put(mc.player, false);

                // 播放音效
                mc.player.level().playSound(null, mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
}