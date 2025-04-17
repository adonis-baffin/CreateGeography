package com.adonis.entity;

import com.adonis.registry.EntityRegistry;
import com.adonis.registry.ItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class ExplosiveArrowEntity extends BaseArrowEntity {

    private static final Item item = ItemRegistry.EXPLOSIVE_ARROW.get();
    private static final float damage = 2.5f; // 基础伤害
    private static final float weight = 0.3f; // 箭矢重量
    private static final float explosionPower = 2.0f; // 爆炸威力，2接近苦力怕

    public ExplosiveArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level, item, damage, weight);
    }

    public ExplosiveArrowEntity(Level level, double x, double y, double z) {
        super(EntityRegistry.EXPLOSIVE_ARROW.get(), level, x, y, z, item, damage, weight);
    }

    public ExplosiveArrowEntity(Level level, LivingEntity shooter) {
        super(EntityRegistry.EXPLOSIVE_ARROW.get(), level, shooter, item, damage, weight);
    }

    @Override
    public void tick() {
        super.tick();
        
        // 添加粒子效果
        if (this.level().isClientSide && !this.inGround) {
            this.level().addParticle(ParticleTypes.SMOKE, 
                    this.getX(), this.getY(), this.getZ(), 
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            explode();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            explode();
        }
    }

    private void explode() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 
                explosionPower, Level.ExplosionInteraction.MOB);
        this.discard();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}