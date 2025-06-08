package com.adonis.content.item;

import com.adonis.content.crafting.CrushingRecipe;
import com.adonis.registry.RecipeRegistry;
import com.adonis.utils.DepotInventoryWrapper;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Optional;

public class GeofragmentatorItem extends Item {

    // 方块交互的常量
    private static final float LOW_HARDNESS_THRESHOLD = 1.0F;
    private static final int BLOCK_COOLDOWN_TICKS = 20; // 1秒

    // 实体攻击的常量
    private static final int ATTACK_COOLDOWN_TICKS = 140; // 7秒
    private static final int SLOW_DURATION_TICKS = 140;   // 7秒
    private static final int SLOW_AMPLIFIER = 1;          // 缓慢 II (放大器为1)
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

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();

        if (!level.isClientSide) {
            if (handleDepotInteraction(player, level, pos, hand)) {
                return InteractionResult.SUCCESS;
            }
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            BlockState blockState = level.getBlockState(pos);

            Optional<CrushingRecipe> recipeOpt = findCrushingRecipe(level, blockState);
            if (recipeOpt.isPresent()) {
                CrushingRecipe recipe = recipeOpt.get();
                List<ItemStack> results = recipe.rollResults(level.random, 0);
                for (ItemStack resultStack : results) {
                    Block.popResource(level, pos, resultStack);
                }

                String soundId = recipe.getSoundEventID();
                if (soundId.isEmpty()) {
                    level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                    SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundId));
                    level.playSound(null, pos, sound != null ? sound : SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                level.destroyBlock(pos, false);
                player.getCooldowns().addCooldown(this, BLOCK_COOLDOWN_TICKS);
                context.getItemInHand().hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            }

            float hardness = blockState.getDestroySpeed(level, pos);
            if (hardness != -1 && hardness <= LOW_HARDNESS_THRESHOLD) {
                level.playSound(null, pos, blockState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                blockState.getBlock().playerDestroy(level, player, pos, blockState, level.getBlockEntity(pos), context.getItemInHand());
                level.removeBlock(pos, false);

                player.getCooldowns().addCooldown(this, BLOCK_COOLDOWN_TICKS);
                context.getItemInHand().hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;
            }
        }

        player.swing(hand);
        BlockState blockState = level.getBlockState(pos);
        level.playSound(player, pos, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.5f, 1.5f);
        if (level.isClientSide) {
            for (int i = 0; i < 5; ++i) {
                level.addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                        pos.getX() + level.random.nextDouble(),
                        pos.getY() + 1,
                        pos.getZ() + level.random.nextDouble(),
                        0.0D, 0.0D, 0.0D
                );
            }
        }

        return InteractionResult.SUCCESS;
    }

    private boolean handleDepotInteraction(Player player, Level level, BlockPos pos, InteractionHand hand) {
        DepotBehaviour depotBehaviour = BlockEntityBehaviour.get(level, pos, DepotBehaviour.TYPE);
        if (depotBehaviour == null) {
            return false;
        }

        ItemStack stackInDepot = depotBehaviour.getHeldItemStack();
        if (stackInDepot.isEmpty()) {
            return false;
        }

        RecipeWrapper wrapper = new RecipeWrapper(new DepotInventoryWrapper(depotBehaviour));
        Optional<PressingRecipe> recipeOpt = AllRecipeTypes.PRESSING.find(wrapper, level);

        if (recipeOpt.isPresent()) {
            Recipe<RecipeWrapper> recipe = recipeOpt.get();
            List<ItemStack> results = RecipeApplier.applyRecipeOn(level, stackInDepot, recipe);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0F, 1.0F);
                serverLevel.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 20, 0.2, 0.1, 0.2, 0.5);
            }

            // --- 最终修正 ---
            ItemStack resultStack = results.isEmpty() ? ItemStack.EMPTY : results.get(0);
            if (!resultStack.isEmpty()) {
                // 将普通的 ItemStack 包装成 Create 的 TransportedItemStack，并调用单参数的 setHeldItem
                depotBehaviour.setHeldItem(new TransportedItemStack(resultStack));
            } else {
                // 如果结果为空，则传入 null 来清空置物台
                depotBehaviour.setHeldItem(null);
            }
            // --- 修正结束 ---

            player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            return true;
        }

        return false;
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