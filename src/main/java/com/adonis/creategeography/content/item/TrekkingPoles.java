package com.adonis.creategeography.content.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class TrekkingPoles extends Item {
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
    private static final UUID JUMP_MODIFIER_UUID = UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3455");
    private static final double SPEED_BONUS = 0.15;
    private static final double JUMP_BONUS = 0.15;

    public TrekkingPoles(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player) || level.isClientSide) {
            return;
        }

        boolean isHeld = player.getMainHandItem().getItem() instanceof TrekkingPoles ||
                player.getOffhandItem().getItem() instanceof TrekkingPoles;

        // Get movement speed attribute
        var movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);

        // Use ENTITY_GRAVITY as a fallback for jump height in 1.20.1
        var jumpHeight = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());



        AttributeModifier speedModifier = new AttributeModifier(
                SPEED_MODIFIER_UUID,
                "Walking Staff Speed Bonus",
                SPEED_BONUS,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        AttributeModifier jumpModifier = null;
        if (jumpHeight != null) {
            jumpModifier = new AttributeModifier(
                    JUMP_MODIFIER_UUID,
                    "Walking Staff Jump Bonus",
                    -JUMP_BONUS, // Negative gravity for higher jump
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
        }

        if (isHeld) {
            // Apply speed modifier
            if (!movementSpeed.hasModifier(speedModifier)) {
                movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
                movementSpeed.addTransientModifier(speedModifier);

            }

            // Apply jump modifier if available
            if (jumpHeight != null && jumpModifier != null && !jumpHeight.hasModifier(jumpModifier)) {
                jumpHeight.removeModifier(JUMP_MODIFIER_UUID);
                jumpHeight.addTransientModifier(jumpModifier);

            }
        } else {
            // Remove speed modifier
            if (movementSpeed.hasModifier(speedModifier)) {
                movementSpeed.removeModifier(SPEED_MODIFIER_UUID);

            }

            // Remove jump modifier if available
            if (jumpHeight != null && jumpModifier != null && jumpHeight.hasModifier(jumpModifier)) {
                jumpHeight.removeModifier(JUMP_MODIFIER_UUID);

            }
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        if (!player.level().isClientSide) {
            var movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            var jumpHeight = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());

            if (movementSpeed != null) {
                movementSpeed.removeModifier(SPEED_MODIFIER_UUID);

            }
            if (jumpHeight != null) {
                jumpHeight.removeModifier(JUMP_MODIFIER_UUID);

            }
        }
        return true;
    }
}