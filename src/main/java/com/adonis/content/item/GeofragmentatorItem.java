package com.adonis.content.item;

import com.adonis.content.crafting.CrushingRecipe;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class GeofragmentatorItem extends Item {

    // (常量定义部分保持不变...)
    private static final float LOW_HARDNESS_THRESHOLD = 1.0F;
    private static final int BLOCK_COOLDOWN_TICKS = 20;
    private static final int ATTACK_COOLDOWN_TICKS = 140;
    private static final int SLOW_DURATION_TICKS = 140;
    private static final int SLOW_AMPLIFIER = 1;
    private static final float BASE_DAMAGE = 14.0F;
    private static final float BASE_KNOCKBACK = 0.8F;
    private static final float JUMP_ATTACK_MULTIPLIER = 1.5F;

    public GeofragmentatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        BlockState blockState = level.getBlockState(pos);

        // 我们在这里返回 PASS，让事件处理器先处理。
        // 如果事件处理器没有取消事件（即不是置物台），那么这个方法仍然会被调用。
        // 所以我们在这里处理所有非置物台的逻辑。

        if (!level.isClientSide) {
            // 1. 检查是否有自定义的破碎配方
            Optional<CrushingRecipe> recipeOpt = findCrushingRecipe(level, blockState);
            if (recipeOpt.isPresent()) {
                CrushingRecipe recipe = recipeOpt.get();
                List<ItemStack> results = recipe.rollResults(level.random, 0);
                for (ItemStack resultStack : results) {
                    Block.popResource(level, pos, resultStack);
                }

                String soundId = recipe.getSoundEventID();
                SoundEvent sound = soundId.isEmpty() ? SoundEvents.STONE_BREAK : ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundId));
                level.playSound(null, pos, sound != null ? sound : SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

                level.destroyBlock(pos, false);
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

    private Optional<CrushingRecipe> findCrushingRecipe(Level level, BlockState blockState) {
        ItemStack blockAsItem = new ItemStack(blockState.getBlock().asItem());
        if (blockAsItem.isEmpty()) return Optional.empty();

        return level.getRecipeManager()
                .getAllRecipesFor(RecipeRegistry.CRUSHING_TYPE.get())
                .stream()
                .filter(recipe -> recipe.getIngredients().get(0).test(blockAsItem))
                .findFirst();
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // (这个方法的代码保持不变)
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

            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    SLOW_DURATION_TICKS,
                    SLOW_AMPLIFIER,
                    false,
                    true));
        }

        int particleCount = isJumpAttack ? (int) (40 * JUMP_ATTACK_MULTIPLIER) : 40;
        double spread = isJumpAttack ? 2.0 * JUMP_ATTACK_MULTIPLIER : 2.0;
        double velocity = isJumpAttack ? 0.5 * JUMP_ATTACK_MULTIPLIER : 0.5;

        if (level.isClientSide) {
            for (int i = 0; i < particleCount; i++) {
                level.addParticle(ParticleTypes.CRIT,
                        target.getX() + (level.random.nextDouble() - 0.5) * spread,
                        target.getY() + target.getBbHeight() * 0.75 + level.random.nextDouble() * 0.5,
                        target.getZ() + (level.random.nextDouble() - 0.5) * spread,
                        (level.random.nextDouble() - 0.5) * velocity,
                        0.3 + level.random.nextDouble() * velocity,
                        (level.random.nextDouble() - 0.5) * velocity);
            }
        }

        float volume1 = isJumpAttack ? 2.5F * JUMP_ATTACK_MULTIPLIER : 2.5F;
        float volume2 = isJumpAttack ? 1.5F * JUMP_ATTACK_MULTIPLIER : 1.5F;
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS,
                volume1, 0.5F + level.random.nextFloat() * 0.2F);
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS,
                volume2, 0.7F + level.random.nextFloat() * 0.3F);

        player.getCooldowns().addCooldown(this, ATTACK_COOLDOWN_TICKS);
        stack.hurtAndBreak(5, player, (p) -> p.broadcastBreakEvent(hand));

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}