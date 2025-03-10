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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class GeologicalHammerItem extends Item {
    private static Map<Block, Block> ROUGH_BLOCK_TO_BEARING;
    private static Map<Block, Block> ICE_TO_CRACKED;
    private static Map<Block, Item> BEARING_TO_POWDER;
    private static boolean mappingsInitialized = false;


    public GeologicalHammerItem(Properties properties) {
        super(properties);
    }

    private void initMappings() {
        if (!mappingsInitialized) {
            synchronized (GeologicalHammerItem.class) {
                if (!mappingsInitialized) {
                    ROUGH_BLOCK_TO_BEARING = new HashMap<>();
                    ICE_TO_CRACKED = new HashMap<>();
                    BEARING_TO_POWDER = new HashMap<>();

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

                        ICE_TO_CRACKED.put(Blocks.ICE, BlockRegistry.CRACKED_ICE.get());
                        ICE_TO_CRACKED.put(Blocks.PACKED_ICE, BlockRegistry.CRACKED_PACKED_ICE.get());
                        ICE_TO_CRACKED.put(Blocks.BLUE_ICE, BlockRegistry.CRACKED_BLUE_ICE.get());

                        BEARING_TO_POWDER.put(BlockRegistry.IRON_BEARING_BLUE_ICE.get(), ItemRegistry.IRON_ORE_POWDER.get());
                        BEARING_TO_POWDER.put(BlockRegistry.COPPER_BEARING_BLUE_ICE.get(), ItemRegistry.COPPER_ORE_POWDER.get());
                        BEARING_TO_POWDER.put(BlockRegistry.GOLD_BEARING_BLUE_ICE.get(), ItemRegistry.GOLD_ORE_POWDER.get());
                        BEARING_TO_POWDER.put(BlockRegistry.COAL_BEARING_BLUE_ICE.get(), ItemRegistry.COAL_POWDER.get());
                        BEARING_TO_POWDER.put(BlockRegistry.LAPIS_LAZULI_BEARING_BLUE_ICE.get(), ItemRegistry.LAPIS_LAZULI_POWDER.get());
                        BEARING_TO_POWDER.put(BlockRegistry.REDSTONE_BEARING_BLUE_ICE.get(), net.minecraft.world.item.Items.REDSTONE);
                        BEARING_TO_POWDER.put(BlockRegistry.ZINC_BEARING_BLUE_ICE.get(), ItemRegistry.ZINC_ORE_POWDER.get());
                        BEARING_TO_POWDER.put(BlockRegistry.OSMIUM_BEARING_BLUE_ICE.get(), ItemRegistry.OSMIUM_ORE_POWDER.get());
                        BEARING_TO_POWDER.put(BlockRegistry.SALT_BEARING_BLUE_ICE.get(), ItemRegistry.SALT.get());
                        BEARING_TO_POWDER.put(BlockRegistry.NITER_BEARING_BLUE_ICE.get(), ItemRegistry.NITER_POWDER.get());

                        mappingsInitialized = true;
                    }
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

        if (ICE_TO_CRACKED.containsKey(block)) {
            Block newBlock = ICE_TO_CRACKED.get(block);
            if (!level.isClientSide) {
                if (block == Blocks.BLUE_ICE) {
                    BlockPos belowPos = pos.below();
                    BlockState belowState = level.getBlockState(belowPos);
                    Block belowBlock = belowState.getBlock();
                    if (ROUGH_BLOCK_TO_BEARING.containsKey(belowBlock)) {
                        newBlock = ROUGH_BLOCK_TO_BEARING.get(belowBlock);
                    }
                }
                level.setBlockAndUpdate(pos, newBlock.defaultBlockState());
                level.playSound(null, pos, newBlock.getSoundType(newBlock.defaultBlockState()).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (block == BlockRegistry.CRACKED_ICE.get() || block == BlockRegistry.CRACKED_PACKED_ICE.get() || block == BlockRegistry.CRACKED_BLUE_ICE.get()) {
            if (!level.isClientSide) {
                Item dropItem = block == BlockRegistry.CRACKED_ICE.get() ? ItemRegistry.ICE_SHARDS.get() :
                        block == BlockRegistry.CRACKED_PACKED_ICE.get() ? ItemRegistry.PACKED_ICE_SHARDS.get() :
                                ItemRegistry.BLUE_ICE_SHARDS.get();
                int dropCount = level.random.nextInt(2, 4);
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(null, pos, block.getSoundType(state).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(dropItem, dropCount));
                level.addFreshEntity(itemEntity);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (BEARING_TO_POWDER.containsKey(block)) {
            if (!level.isClientSide) {
                Item dropItem = BEARING_TO_POWDER.get(block);
                int dropCount = level.random.nextInt(1, 3);
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(null, pos, block.getSoundType(state).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(dropItem, dropCount));
                level.addFreshEntity(itemEntity);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }

        // 修改后的跳跃攻击判定（只要在空中即可）
        boolean isJumpAttack = !player.onGround(); // 移除了下落速度判断

        if (!level.isClientSide) {
            // 根据是否空中攻击计算伤害（基础14点，空中攻击+50%）
            float damage = isJumpAttack ? 14.0F * 1.5F : 14.0F;
            target.hurt(level.damageSources().playerAttack(player), damage);

            // 击退效果增强（基础0.8，空中攻击+50%）
            float knockback = isJumpAttack ? 0.8F * 1.5F : 0.8F;
            target.knockback(knockback,
                    player.getX() - target.getX(),
                    player.getZ() - target.getZ());
        }

        // 强化粒子效果（基础40个粒子，空中攻击+50%）
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

        // ...（后续代码保持不变）


        // 音效增强（基础音量2.5F/1.5F，跳劈+50%）
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

        player.getCooldowns().addCooldown(this, 50);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}