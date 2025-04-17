package com.adonis.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class BaseArrowEntity extends AbstractArrow {
    
    private final Item item;
    private final float baseDamage;
    private final float weight;

    protected BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level, Item item, float damage, float weight) {
        super(entityType, level);
        this.item = item;
        this.baseDamage = damage;
        this.weight = weight;
        this.setBaseDamage(damage);
    }

    protected BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level, double x, double y, double z, Item item, float damage, float weight) {
        super(entityType, x, y, z, level);
        this.item = item;
        this.baseDamage = damage;
        this.weight = weight;
        this.setBaseDamage(damage);
    }

    protected BaseArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level, LivingEntity shooter, Item item, float damage, float weight) {
        super(entityType, shooter, level);
        this.item = item;
        this.baseDamage = damage;
        this.weight = weight;
        this.setBaseDamage(damage);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(this.item);
    }

    @Override
    public void tick() {
        super.tick();
        // 可以在这里添加特殊效果，如粒子效果
    }
    
    // 获取箭矢重量，影响飞行轨迹
    public float getWeight() {
        return this.weight;
    }
}