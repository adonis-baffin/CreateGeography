package com.adonis.content.item;

import com.adonis.content.crafting.FragmentingRecipe;
import com.adonis.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class GeofragmentatorItem extends Item {

    // 常量
    private static final float LOW_HARDNESS_THRESHOLD = 1.0F;
    private static final int BLOCK_COOLDOWN_TICKS = 25;
    private static final int ATTACK_COOLDOWN_TICKS = 50; // 缩短CD
    private static final float BASE_DAMAGE = 14.0F;
    private static final float BASE_KNOCKBACK = 0.8F;
    private static final float JUMP_ATTACK_MULTIPLIER = 1.5F;

    // 粒子效果常量
    private static final int ATTACK_PARTICLE_COUNT = 15;
    private static final double PARTICLE_SPREAD = 1.5;
    private static final double PARTICLE_VELOCITY = 0.4;

    public GeofragmentatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        BlockState blockState = level.getBlockState(pos);

        if (!level.isClientSide) {
            // 1. 检查是否有自定义的破碎配方
            Optional<FragmentingRecipe> recipeOpt = findFragmentingRecipe(level, blockState);
            if (recipeOpt.isPresent()) {
                FragmentingRecipe recipe = recipeOpt.get();

                // 应用配方：掉落物
                List<ItemStack> results = recipe.rollResults(level.random, 0);
                for (ItemStack resultStack : results) {
                    Block.popResource(level, pos, resultStack);
                }

                // 应用配方：方块转变
                level.setBlock(pos, recipe.getOutputBlock(), 3);

                // 播放声音
                String soundId = recipe.getSoundEventID();
                SoundEvent sound = soundId.isEmpty() ? blockState.getSoundType().getBreakSound() : ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundId));
                level.playSound(null, pos, sound != null ? sound : SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

                player.getCooldowns().addCooldown(this, BLOCK_COOLDOWN_TICKS);
                context.getItemInHand().hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            }

            // 2. 如果没有配方，检查方块硬度
            float hardness = blockState.getDestroySpeed(level, pos);
            if (hardness != -1.0F && hardness <= LOW_HARDNESS_THRESHOLD) {
                level.playSound(null, pos, blockState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                blockState.getBlock().playerDestroy(level, player, pos, blockState, level.getBlockEntity(pos), context.getItemInHand());
                level.removeBlock(pos, false);

                player.getCooldowns().addCooldown(this, BLOCK_COOLDOWN_TICKS);
                context.getItemInHand().hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            }
        }

        // 3. 高硬度方块，只播放效果
        player.swing(hand);
        level.playSound(player, pos, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.5f, 1.5f);
        if (level.isClientSide) {
            for (int i = 0; i < 5; ++i) {
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), pos.getX() + level.random.nextDouble(), pos.getY() + 1, pos.getZ() + level.random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private Optional<FragmentingRecipe> findFragmentingRecipe(Level level, BlockState blockState) {
        ItemStack blockAsItem = new ItemStack(blockState.getBlock().asItem());
        if (blockAsItem.isEmpty()) return Optional.empty();

        return level.getRecipeManager()
                .getAllRecipesFor(RecipeRegistry.FRAGMENTING_TYPE.get())
                .stream()
                .filter(recipe -> recipe.matches(blockState))
                .findFirst();
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }

        boolean isJumpAttack = !player.onGround() && player.fallDistance > 0.0F && !player.isPassenger();

        if (!level.isClientSide) {
            float damage = isJumpAttack ? BASE_DAMAGE * JUMP_ATTACK_MULTIPLIER : BASE_DAMAGE;
            target.hurt(level.damageSources().playerAttack(player), damage);

            float knockback = isJumpAttack ? BASE_KNOCKBACK * JUMP_ATTACK_MULTIPLIER : BASE_KNOCKBACK;
            target.knockback(knockback,
                    player.getX() - target.getX(),
                    player.getZ() - target.getZ());

            // 移除减速效果
        }

        // 减少粒子效果
        int particleCount = isJumpAttack ? (int) (ATTACK_PARTICLE_COUNT * JUMP_ATTACK_MULTIPLIER) : ATTACK_PARTICLE_COUNT;

        if (level.isClientSide) {
            for (int i = 0; i < particleCount; i++) {
                level.addParticle(ParticleTypes.CRIT,
                        target.getX() + (level.random.nextDouble() - 0.5) * PARTICLE_SPREAD,
                        target.getY() + target.getBbHeight() * 0.75 + level.random.nextDouble() * 0.5,
                        target.getZ() + (level.random.nextDouble() - 0.5) * PARTICLE_SPREAD,
                        (level.random.nextDouble() - 0.5) * PARTICLE_VELOCITY,
                        0.3 + level.random.nextDouble() * PARTICLE_VELOCITY,
                        (level.random.nextDouble() - 0.5) * PARTICLE_VELOCITY);
            }
        }

        // 声音效果保持不变
        float volume1 = isJumpAttack ? 2.5F * JUMP_ATTACK_MULTIPLIER : 2.5F;
        float volume2 = isJumpAttack ? 1.5F * JUMP_ATTACK_MULTIPLIER : 1.5F;
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, volume1, 0.5F + level.random.nextFloat() * 0.2F);
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, volume2, 0.7F + level.random.nextFloat() * 0.3F);

        player.getCooldowns().addCooldown(this, ATTACK_COOLDOWN_TICKS);
        stack.hurtAndBreak(2, player, (p) -> p.broadcastBreakEvent(hand));

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}