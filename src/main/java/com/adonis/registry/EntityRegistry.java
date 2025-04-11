package com.adonis.registry;

import com.adonis.CreateGeography;
import com.adonis.entity.EnderCrystalArrowEntity;
import com.adonis.entity.ExplosiveArrowEntity;
import com.adonis.entity.LightningArrowEntity;
import com.adonis.entity.ThrownPebbleEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CreateGeography.MODID);
    
    // 注册投掷的卵石实体
    public static final RegistryObject<EntityType<ThrownPebbleEntity>> THROWN_PEBBLE = ENTITIES.register("thrown_pebble",
            () -> EntityType.Builder.<ThrownPebbleEntity>of(ThrownPebbleEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F) // 设置实体大小
                    .clientTrackingRange(4) // 客户端追踪范围
                    .updateInterval(10) // 更新间隔
                    .build(new ResourceLocation(CreateGeography.MODID, "thrown_pebble").toString()));

// ... 现有代码 ...

    // 注册爆炸箭实体
    public static final RegistryObject<EntityType<ExplosiveArrowEntity>> EXPLOSIVE_ARROW = ENTITIES.register(
            "explosive_arrow",
            () -> EntityType.Builder.<ExplosiveArrowEntity>of(ExplosiveArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("explosive_arrow")
    );

    // ... 现有代码 ...

    // 注册末影水晶爆炸箭实体
    public static final RegistryObject<EntityType<EnderCrystalArrowEntity>> ENDER_CRYSTAL_ARROW = ENTITIES.register(
            "ender_crystal_arrow",
            () -> EntityType.Builder.<EnderCrystalArrowEntity>of((type, world) -> new EnderCrystalArrowEntity(type, world), net.minecraft.world.entity.MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("ender_crystal_arrow")
    );

    // ... 现有代码 ...

    // 注册闪电箭实体
    public static final RegistryObject<EntityType<LightningArrowEntity>> LIGHTNING_ARROW = ENTITIES.register(
            "lightning_arrow",
            () -> EntityType.Builder.<LightningArrowEntity>of((type, world) -> new LightningArrowEntity(type, world), net.minecraft.world.entity.MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("lightning_arrow")
    );

// ... 现有代码 ...

// ... 现有代码 ...

// ... 现有代码 ...
}