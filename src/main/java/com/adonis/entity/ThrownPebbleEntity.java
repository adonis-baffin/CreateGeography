package com.adonis.entity;

import com.adonis.registry.EntityRegistry;
import com.adonis.registry.ItemRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownPebbleEntity extends ThrowableItemProjectile {
    
    public ThrownPebbleEntity(EntityType<? extends ThrownPebbleEntity> entityType, Level level) {
        super(entityType, level);
    }
    
    public ThrownPebbleEntity(Level level, LivingEntity entity) {
        super(EntityRegistry.THROWN_PEBBLE.get(), entity, level);
    }
    
    public ThrownPebbleEntity(Level level, double x, double y, double z) {
        super(EntityRegistry.THROWN_PEBBLE.get(), x, y, z, level);
    }
    
    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.PEBBLE.get();
    }
    
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ItemStack itemstack = this.getItemRaw();
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), 
                        this.getX(), this.getY(), this.getZ(), 
                        ((double)this.random.nextFloat() - 0.5D) * 0.08D, 
                        ((double)this.random.nextFloat() - 0.5D) * 0.08D, 
                        ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }
    
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        
        // 造成5点伤害
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 5.0F);
    }
    
    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }
}