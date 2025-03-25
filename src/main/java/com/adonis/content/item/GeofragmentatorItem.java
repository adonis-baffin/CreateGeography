package com.adonis.content.item;

import com.adonis.registry.BlockRegistry;
import com.adonis.registry.ItemRegistry;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeofragmentatorItem extends Item {
    private static Map<Block, Block> ROUGH_BLOCK_TO_BEARING; // 含矿蓝冰映射
    private static Map<Block, Block> BLOCK_TO_INTERMEDIATE; // 轮次2方块到中间态映射
    private static Map<Block, DropConfig> INTERMEDIATE_TO_DROPS; // 中间态到掉落物映射
    private static Map<Block, DropConfig> DIRECT_DROPS; // 轮次1方块直接掉落映射
    private static boolean mappingsInitialized = false;

    public GeofragmentatorItem(Properties properties) {
        super(properties);
    }

    private void initMappings() {
        if (!mappingsInitialized) {
            synchronized (GeofragmentatorItem.class) {
                if (!mappingsInitialized) {
                    ROUGH_BLOCK_TO_BEARING = new HashMap<>();
                    BLOCK_TO_INTERMEDIATE = new HashMap<>();
                    INTERMEDIATE_TO_DROPS = new HashMap<>();
                    DIRECT_DROPS = new HashMap<>();

                    // 含矿蓝冰映射（保持不变）
                    if (BlockRegistry.IRON_BEARING_BLUE_ICE.isPresent() && ItemRegistry.IRON_ORE_POWDER.isPresent()) {
                        ROUGH_BLOCK_TO_BEARING.put(Blocks.RAW_IRON_BLOCK, BlockRegistry.IRON_BEARING_BLUE_ICE.get());
                        ROUGH_BLOCK_TO_BEARING.put(Blocks.RAW_COPPER_BLOCK, BlockRegistry.COPPER_BEARING_BLUE_ICE.get());
                        ROUGH_BLOCK_TO_BEARING.put(Blocks.RAW_GOLD_BLOCK, BlockRegistry.GOLD_BEARING_BLUE_ICE.get());
                        ROUGH_BLOCK_TO_BEARING.put(Blocks.COAL_BLOCK, BlockRegistry.COAL_BEARING_BLUE_ICE.get());
                        ROUGH_BLOCK_TO_BEARING.put(Blocks.LAPIS_BLOCK, BlockRegistry.LAPIS_LAZULI_BEARING_BLUE_ICE.get());
                        ROUGH_BLOCK_TO_BEARING.put(Blocks.REDSTONE_BLOCK, BlockRegistry.REDSTONE_BEARING_BLUE_ICE.get());
                        ROUGH_BLOCK_TO_BEARING.put(AllBlocks.RAW_ZINC_BLOCK.get(), BlockRegistry.ZINC_BEARING_BLUE_ICE.get());
                        ROUGH_BLOCK_TO_BEARING.put(Blocks.DEEPSLATE_COPPER_ORE, BlockRegistry.OSMIUM_BEARING_BLUE_ICE.get());
                        ROUGH_BLOCK_TO_BEARING.put(BlockRegistry.SALT_BLOCK.get(), BlockRegistry.SALT_BEARING_BLUE_ICE.get());
                        ROUGH_BLOCK_TO_BEARING.put(BlockRegistry.NITER_BLOCK.get(), BlockRegistry.NITER_BEARING_BLUE_ICE.get());
                    }

                    // 轮次2方块到中间态映射
                    BLOCK_TO_INTERMEDIATE.put(Blocks.STONE, Blocks.COBBLESTONE);
                    BLOCK_TO_INTERMEDIATE.put(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
                    BLOCK_TO_INTERMEDIATE.put(Blocks.BASALT, BlockRegistry.CRACKED_BASALT.get());
                    BLOCK_TO_INTERMEDIATE.put(Blocks.BLACKSTONE, BlockRegistry.CRACKED_BLACKSTONE.get());
                    BLOCK_TO_INTERMEDIATE.put(Blocks.ANDESITE, BlockRegistry.CRACKED_ANDESITE.get());
                    BLOCK_TO_INTERMEDIATE.put(Blocks.GRANITE, BlockRegistry.CRACKED_GRANITE.get());
                    BLOCK_TO_INTERMEDIATE.put(Blocks.DIORITE, BlockRegistry.CRACKED_DIORITE.get());
                    BLOCK_TO_INTERMEDIATE.put(Blocks.ICE, BlockRegistry.CRACKED_ICE.get());
                    BLOCK_TO_INTERMEDIATE.put(Blocks.PACKED_ICE, BlockRegistry.CRACKED_PACKED_ICE.get());
                    BLOCK_TO_INTERMEDIATE.put(Blocks.BLUE_ICE, BlockRegistry.CRACKED_BLUE_ICE.get());
                    BLOCK_TO_INTERMEDIATE.put(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW);
                    BLOCK_TO_INTERMEDIATE.put(Blocks.GRASS, Blocks.AIR);
                    BLOCK_TO_INTERMEDIATE.put(Blocks.TALL_GRASS, Blocks.AIR);

                    // 中间态到掉落物映射
                    INTERMEDIATE_TO_DROPS.put(Blocks.COBBLESTONE, new DropConfig()
                            .addDrop(ItemRegistry.CRUSHED_STONE.get(), 1.0f, 3)
                            .addDrop(ItemRegistry.PEBBLE.get(), 1.0f, 1));
                    INTERMEDIATE_TO_DROPS.put(Blocks.COBBLED_DEEPSLATE, new DropConfig()
                            .addDrop(ItemRegistry.CRUSHED_DEEP_SLATE.get(), 1.0f, 3)
                            .addDrop(ItemRegistry.DEEP_SLATE_PEBBLE.get(), 1.0f, 1));
                    INTERMEDIATE_TO_DROPS.put(BlockRegistry.CRACKED_BASALT.get(), new DropConfig()
                            .addDrop(ItemRegistry.CRUSHED_STONE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.BIOTITE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.PLAGIOCLASE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.ASH.get(), 0.95f, 1)
                            .addDrop(ItemRegistry.PYROXENE.get(), 0.05f, 1));
                    INTERMEDIATE_TO_DROPS.put(BlockRegistry.CRACKED_BLACKSTONE.get(), new DropConfig()
                            .addDrop(ItemRegistry.CRUSHED_STONE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.BIOTITE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.COAL_POWDER.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.ASH.get(), 0.95f, 1)
                            .addDrop(ItemRegistry.HORNBLENDE.get(), 0.05f, 1));
                    INTERMEDIATE_TO_DROPS.put(BlockRegistry.CRACKED_ANDESITE.get(), new DropConfig()
                            .addDrop(ItemRegistry.CRUSHED_STONE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.PLAGIOCLASE.get(), 1.0f, 2)
                            .addDrop(ItemRegistry.PLAGIOCLASE.get(), 0.85f, 1)
                            .addDrop(ItemRegistry.HORNBLENDE.get(), 0.10f, 1)
                            .addDrop(ItemRegistry.PYROXENE.get(), 0.05f, 1));
                    INTERMEDIATE_TO_DROPS.put(BlockRegistry.CRACKED_GRANITE.get(), new DropConfig()
                            .addDrop(ItemRegistry.CRUSHED_STONE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.ORTHACLASE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.PLAGIOCLASE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.QUARTZ_SAND.get(), 0.95f, 1)
                            .addDrop(ItemRegistry.HORNBLENDE.get(), 0.05f, 1));
                    INTERMEDIATE_TO_DROPS.put(BlockRegistry.CRACKED_DIORITE.get(), new DropConfig()
                            .addDrop(ItemRegistry.CRUSHED_STONE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.PLAGIOCLASE.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.QUARTZ_SAND.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.QUARTZ_SAND.get(), 0.80f, 1)
                            .addDrop(ItemRegistry.HORNBLENDE.get(), 0.20f, 1));
                    INTERMEDIATE_TO_DROPS.put(BlockRegistry.CRACKED_ICE.get(), new DropConfig()
                            .addDrop(ItemRegistry.ICE_SHARDS.get(), 1.0f, 4));
                    INTERMEDIATE_TO_DROPS.put(BlockRegistry.CRACKED_PACKED_ICE.get(), new DropConfig()
                            .addDrop(ItemRegistry.PACKED_ICE_SHARDS.get(), 1.0f, 4));
                    INTERMEDIATE_TO_DROPS.put(BlockRegistry.CRACKED_BLUE_ICE.get(), new DropConfig()
                            .addDrop(ItemRegistry.BLUE_ICE_SHARDS.get(), 1.0f, 4));

                    // 轮次1方块直接掉落映射
                    DIRECT_DROPS.put(Blocks.COBBLESTONE, new DropConfig()
                            .addDrop(ItemRegistry.PEBBLE.get(), 1.0f, 3)
                            .addDrop(ItemRegistry.CRUSHED_STONE.get(), 1.0f, 1));
                    DIRECT_DROPS.put(Blocks.MOSSY_COBBLESTONE, new DropConfig() // 苔石与圆石相同
                            .addDrop(ItemRegistry.PEBBLE.get(), 1.0f, 3)
                            .addDrop(ItemRegistry.CRUSHED_STONE.get(), 1.0f, 1));
                    DIRECT_DROPS.put(Blocks.SANDSTONE, new DropConfig()
                            .addDrop(ItemRegistry.SAND_DUST.get(), 1.0f, 3)
                            .addDrop(ItemRegistry.SAND_DUST.get(), 0.95f, 1)
                            .addDrop(ItemRegistry.NITER.get(), 0.05f, 1));
                    DIRECT_DROPS.put(Blocks.RED_SANDSTONE, new DropConfig()
                            .addDrop(ItemRegistry.RED_SAND_DUST.get(), 1.0f, 3)
                            .addDrop(ItemRegistry.RED_SAND_DUST.get(), 0.95f, 1)
                            .addDrop(ItemRegistry.NITER.get(), 0.05f, 1));
                    DIRECT_DROPS.put(Blocks.TUFF, new DropConfig()
                            .addDrop(ItemRegistry.ASH.get(), 1.0f, 3)
                            .addDrop(ItemRegistry.CRUSHED_STONE.get(), 0.95f, 1)
                            .addDrop(ItemRegistry.NITER.get(), 0.05f, 1));
                    DIRECT_DROPS.put(Blocks.GLOWSTONE, new DropConfig()
                            .addDrop(net.minecraft.world.item.Items.GLOWSTONE_DUST, 1.0f, 4));
                    DIRECT_DROPS.put(Blocks.AMETHYST_BLOCK, new DropConfig()
                            .addDrop(net.minecraft.world.item.Items.AMETHYST_SHARD, 1.0f, 4));
                    DIRECT_DROPS.put(BlockRegistry.SALT_BLOCK.get(), new DropConfig()
                            .addDrop(ItemRegistry.SALT.get(), 1.0f, 4));
                    DIRECT_DROPS.put(BlockRegistry.NITER_BLOCK.get(), new DropConfig()
                            .addDrop(ItemRegistry.NITER.get(), 1.0f, 4));
                    DIRECT_DROPS.put(Blocks.DIRT, new DropConfig()
                            .addDrop(ItemRegistry.DIRT_CLOD.get(), 1.0f, 4));
                    DIRECT_DROPS.put(Blocks.COARSE_DIRT, new DropConfig()
                            .addDrop(ItemRegistry.DIRT_CLOD.get(), 1.0f, 4));
                    DIRECT_DROPS.put(Blocks.GRASS_BLOCK, new DropConfig()
                            .addDrop(ItemRegistry.DIRT_CLOD.get(), 1.0f, 4));
                    DIRECT_DROPS.put(Blocks.PODZOL, new DropConfig() // 灰化土与泥土相同
                            .addDrop(ItemRegistry.DIRT_CLOD.get(), 1.0f, 4));
                    DIRECT_DROPS.put(Blocks.RED_SAND, new DropConfig()
                            .addDrop(ItemRegistry.RED_SAND_DUST.get(), 1.0f, 4));
                    DIRECT_DROPS.put(Blocks.SAND, new DropConfig()
                            .addDrop(ItemRegistry.SAND_DUST.get(), 1.0f, 4));
                    DIRECT_DROPS.put(Blocks.GRAVEL, new DropConfig()
                            .addDrop(ItemRegistry.SAND_DUST.get(), 1.0f, 1)
                            .addDrop(ItemRegistry.ASH.get(), 1.0f, 2)
                            .addDrop(ItemRegistry.ASH.get(), 0.9f, 1)
                            .addDrop(Items.FLINT, 0.1f, 1));

                    mappingsInitialized = true;
                }
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        initMappings();
        if (!mappingsInitialized) {
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (!level.isClientSide) {
            // 处理轮次2方块（第一次敲击）
            if (BLOCK_TO_INTERMEDIATE.containsKey(block)) {
                Block intermediateBlock = BLOCK_TO_INTERMEDIATE.get(block);
                // 对于蓝冰，检查下方方块以决定是否生成含矿蓝冰
                if (block == Blocks.BLUE_ICE && ROUGH_BLOCK_TO_BEARING != null) {
                    BlockPos belowPos = pos.below();
                    BlockState belowState = level.getBlockState(belowPos);
                    Block belowBlock = belowState.getBlock();
                    if (ROUGH_BLOCK_TO_BEARING.containsKey(belowBlock)) {
                        intermediateBlock = ROUGH_BLOCK_TO_BEARING.get(belowBlock);
                    }
                }
                level.setBlockAndUpdate(pos, intermediateBlock.defaultBlockState());
                level.playSound(null, pos, intermediateBlock.getSoundType(intermediateBlock.defaultBlockState()).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
            // 处理中间态方块（第二次敲击）
            else if (INTERMEDIATE_TO_DROPS.containsKey(block)) {
                DropConfig drops = INTERMEDIATE_TO_DROPS.get(block);
                for (DropItem drop : drops.getDrops()) {
                    dropWithChance(level, pos, drop.item, drop.chance, drop.count);
                }
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(null, pos, block.getSoundType(state).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
            // 处理轮次1方块（直接掉落）
            else if (DIRECT_DROPS.containsKey(block)) {
                DropConfig drops = DIRECT_DROPS.get(block);
                for (DropItem drop : drops.getDrops()) {
                    dropWithChance(level, pos, drop.item, drop.chance, drop.count);
                }
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(null, pos, block.getSoundType(state).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
            // 处理含矿蓝冰（保持不变）
            else if (block == BlockRegistry.IRON_BEARING_BLUE_ICE.get() ||
                    block == BlockRegistry.COPPER_BEARING_BLUE_ICE.get() ||
                    block == BlockRegistry.GOLD_BEARING_BLUE_ICE.get() ||
                    block == BlockRegistry.COAL_BEARING_BLUE_ICE.get() ||
                    block == BlockRegistry.LAPIS_LAZULI_BEARING_BLUE_ICE.get() ||
                    block == BlockRegistry.REDSTONE_BEARING_BLUE_ICE.get() ||
                    block == BlockRegistry.ZINC_BEARING_BLUE_ICE.get() ||
                    block == BlockRegistry.OSMIUM_BEARING_BLUE_ICE.get() ||
                    block == BlockRegistry.SALT_BEARING_BLUE_ICE.get() ||
                    block == BlockRegistry.NITER_BEARING_BLUE_ICE.get()) {
                Item dropItem = block == BlockRegistry.IRON_BEARING_BLUE_ICE.get() ? ItemRegistry.IRON_ORE_POWDER.get() :
                        block == BlockRegistry.COPPER_BEARING_BLUE_ICE.get() ? ItemRegistry.COPPER_ORE_POWDER.get() :
                                block == BlockRegistry.GOLD_BEARING_BLUE_ICE.get() ? ItemRegistry.GOLD_ORE_POWDER.get() :
                                        block == BlockRegistry.COAL_BEARING_BLUE_ICE.get() ? ItemRegistry.COAL_POWDER.get() :
                                                block == BlockRegistry.LAPIS_LAZULI_BEARING_BLUE_ICE.get() ? ItemRegistry.LAPIS_LAZULI_POWDER.get() :
                                                        block == BlockRegistry.REDSTONE_BEARING_BLUE_ICE.get() ? net.minecraft.world.item.Items.REDSTONE :
                                                                block == BlockRegistry.ZINC_BEARING_BLUE_ICE.get() ? ItemRegistry.ZINC_ORE_POWDER.get() :
                                                                        block == BlockRegistry.OSMIUM_BEARING_BLUE_ICE.get() ? ItemRegistry.OSMIUM_ORE_POWDER.get() :
                                                                                block == BlockRegistry.SALT_BEARING_BLUE_ICE.get() ? ItemRegistry.SALT.get() :
                                                                                        ItemRegistry.NITER_POWDER.get();
                int dropCount = level.random.nextInt(1, 3);
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(null, pos, block.getSoundType(state).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(dropItem, dropCount));
                level.addFreshEntity(itemEntity);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }

        boolean isJumpAttack = !player.onGround();

        if (!level.isClientSide) {
            float damage = isJumpAttack ? 14.0F * 1.5F : 14.0F;
            target.hurt(level.damageSources().playerAttack(player), damage);

            float knockback = isJumpAttack ? 0.8F * 1.5F : 0.8F;
            target.knockback(knockback,
                    player.getX() - target.getX(),
                    player.getZ() - target.getZ());

            // 添加缓慢II效果，持续4秒（80 ticks）
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, // 缓慢效果
                    140, // 持续时间：4秒 = 80 ticks
                    1, // 等级：缓慢II (从0开始计数，1表示II)
                    false, // 是否环境效果
                    false)); // 是否显示粒子
        }

        if (level.isClientSide) {
            int particleCount = isJumpAttack ? (int)(40 * 1.5) : 40;
            double spread = isJumpAttack ? 2.0 * 1.5 : 2.0;
            double velocity = isJumpAttack ? 0.5 * 1.5 : 0.5;

            for (int i = 0; i < particleCount; i++) {
                level.addParticle(ParticleTypes.CRIT,
                        target.getX() + (level.random.nextDouble() - 0.5) * spread,
                        target.getY() + 0.8 + level.random.nextDouble() * 1.5,
                        target.getZ() + (level.random.nextDouble() - 0.5) * spread,
                        (level.random.nextDouble() - 0.5) * velocity,
                        0.3 + level.random.nextDouble() * velocity,
                        (level.random.nextDouble() - 0.5) * velocity);
            }
        }

        float[] volumes = {
                isJumpAttack ? 2.5F * 1.5F : 2.5F,
                isJumpAttack ? 1.5F * 1.5F : 1.5F
        };

        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS,
                volumes[0], 0.5F + level.random.nextFloat() * 0.2F);

        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS,
                volumes[1], 0.7F + level.random.nextFloat() * 0.3F);

        player.getCooldowns().addCooldown(this, 140);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // 辅助方法：掉落物品
    private void dropWithChance(Level level, BlockPos pos, Item item, float chance, int count) {
        if (level.random.nextFloat() <= chance) {
            Block.popResource(level, pos, new ItemStack(item, count));
        }
    }

    // DropConfig 类，用于配置掉落物
    private static class DropConfig {
        private final List<DropItem> drops = new ArrayList<>();

        public DropConfig addDrop(Item item, float chance, int count) {
            drops.add(new DropItem(item, chance, count));
            return this;
        }

        public List<DropItem> getDrops() {
            return drops;
        }
    }

    // DropItem 类，表示单个掉落物
    private static class DropItem {
        final Item item;
        final float chance;
        final int count;

        DropItem(Item item, float chance, int count) {
            this.item = item;
            this.chance = chance;
            this.count = count;
        }
    }
}