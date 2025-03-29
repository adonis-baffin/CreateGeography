package com.adonis.content.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class SickleItem extends Item implements Vanishable {
    private final Tier tier;
    private final float attackDamage;
    private final float attackSpeed;

    // 攻击伤害修饰符UUID
    private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    // 触及距离修饰符UUID
    private static final UUID REACH_DISTANCE_MODIFIER_UUID = UUID.fromString("5D4F0E1A-8B3F-4B4A-8D1E-3B4B7A4B1E2F");
    private static final UUID ATTACK_RANGE_MODIFIER_UUID = UUID.fromString("6D4F0E1A-8B3F-4B4A-8D1E-3B4B7A4B1E2F");

    // 直接使用资源位置获取属性
    private static final ResourceLocation REACH_DISTANCE_RL = new ResourceLocation("forge", "reach_distance");
    private static final ResourceLocation ATTACK_RANGE_RL = new ResourceLocation("forge", "attack_range");

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public SickleItem(Tier tier, float attackDamage, Properties properties) {
        super(properties.durability(tier.getUses()));
        this.tier = tier;
        this.attackDamage = attackDamage;
        this.attackSpeed = 1.4F;

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADDITION));

        // 使用资源位置获取属性
        Attribute reachDistance = ForgeRegistries.ATTRIBUTES.getValue(REACH_DISTANCE_RL);
        Attribute attackRange = ForgeRegistries.ATTRIBUTES.getValue(ATTACK_RANGE_RL);

        if (reachDistance != null) {
            builder.put(reachDistance, new AttributeModifier(REACH_DISTANCE_MODIFIER_UUID, "Sickle reach bonus", 2.5D, AttributeModifier.Operation.ADDITION));
        }

        if (attackRange != null) {
            builder.put(attackRange, new AttributeModifier(ATTACK_RANGE_MODIFIER_UUID, "Sickle attack range bonus", 2.5D, AttributeModifier.Operation.ADDITION));
        }

        this.defaultModifiers = builder.build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(attacker.getUsedItemHand()));

        if (target instanceof Animal) {
            int lootingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, stack);

            if (target.getRandom().nextFloat() < 0.5F + (lootingLevel * 0.1F)) {
                target.spawnAtLocation(target.getPickResult());
            }

            // 暴击伤害
            float critDamage = attackDamage * 1.75F; // 暴击伤害为基础伤害的1.75倍
            target.hurt(target.damageSources().generic(), critDamage);
        }

        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        Block block = state.getBlock();

        // 加速挖掘特定方块
        if (block == Blocks.CAKE ||
                block == Blocks.COBWEB ||
                state.is(BlockTags.WOOL) ||
                state.is(BlockTags.LEAVES) ||
                block == Blocks.HAY_BLOCK ||
                block == Blocks.TARGET) {
            return 15.0F;
        }

        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(1, entity, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        // 允许的魔咒
        return enchantment == Enchantments.SHARPNESS ||
                enchantment == Enchantments.SMITE ||
                enchantment == Enchantments.BANE_OF_ARTHROPODS ||
                enchantment == Enchantments.KNOCKBACK ||
                enchantment == Enchantments.FIRE_ASPECT ||
                enchantment == Enchantments.MOB_LOOTING ||
                enchantment == Enchantments.BLOCK_EFFICIENCY ||
                enchantment == Enchantments.UNBREAKING ||
                enchantment == Enchantments.MENDING ||
                super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }
}