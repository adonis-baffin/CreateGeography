package com.adonis.event;

import com.adonis.CreateGeography;
import com.adonis.content.item.GeofragmentatorItem;
import com.adonis.networking.ModMessages;
import com.adonis.networking.packet.BasinProcessingPacket;
import com.adonis.networking.packet.DepotProcessingPacket;
import com.adonis.utils.DepotInventoryWrapper;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = CreateGeography.MODID)
public class GeofragmentatorInteractionHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!(player.getItemInHand(event.getHand()).getItem() instanceof GeofragmentatorItem)) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        // 尝试获取 DepotBehaviour
        DepotBehaviour depotBehaviour = BlockEntityBehaviour.get(level, pos, DepotBehaviour.TYPE);
        if (depotBehaviour != null) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            if (!level.isClientSide) {
                handleDepotInteraction(player, event.getHand(), depotBehaviour);
            }
            return;
        }

        // 尝试获取 BasinBlockEntity
        if (level.getBlockEntity(pos) instanceof BasinBlockEntity basin) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            if (!level.isClientSide) {
                handleBasinInteraction(player, event.getHand(), basin);
            }
        }
    }

    private static void handleDepotInteraction(Player player, InteractionHand hand, DepotBehaviour depotBehaviour) {
        ItemStack stackInDepot = depotBehaviour.getHeldItemStack();
        if (stackInDepot.isEmpty()) {
            return;
        }

        RecipeWrapper wrapper = new RecipeWrapper(new DepotInventoryWrapper(depotBehaviour));
        Optional<PressingRecipe> recipeOpt = AllRecipeTypes.PRESSING.find(wrapper, depotBehaviour.getWorld());
        if (recipeOpt.isEmpty()) {
            return;
        }

        Recipe<RecipeWrapper> recipe = recipeOpt.get();

        boolean processEntireStack = player.isShiftKeyDown();
        int amountToProcess = processEntireStack ? stackInDepot.getCount() : 1;

        ItemStack inputForRecipe = ItemHandlerHelper.copyStackWithSize(stackInDepot, amountToProcess);
        List<ItemStack> results = RecipeApplier.applyRecipeOn(depotBehaviour.getWorld(), inputForRecipe, recipe);

        if (results.isEmpty()) {
            return;
        }

        ItemStack resultStack = results.get(0);

        ItemStack newStackOnDepot = stackInDepot.copy();
        newStackOnDepot.shrink(amountToProcess);

        if (!newStackOnDepot.isEmpty()) {
            depotBehaviour.setHeldItem(new TransportedItemStack(newStackOnDepot));
        } else {
            depotBehaviour.setHeldItem(null);
        }

        ItemHandlerHelper.giveItemToPlayer(player, resultStack);

        Level level = depotBehaviour.getWorld();
        BlockPos pos = depotBehaviour.getPos();

        level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0F, 1.0F);
        ModMessages.sendToClients(new DepotProcessingPacket(pos, stackInDepot));
        player.swing(hand);
        player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        depotBehaviour.blockEntity.notifyUpdate();
    }

    private static void handleBasinInteraction(Player player, InteractionHand hand, BasinBlockEntity basin) {
        Level level = basin.getLevel();
        if (level == null) return;

        List<CompactingRecipe> compactingRecipes = level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.COMPACTING.getType());

        // 明确 Optional 的类型为 CompactingRecipe
        Optional<CompactingRecipe> recipeOpt = compactingRecipes.stream()
                .filter(recipe -> BasinRecipe.match(basin, recipe))
                .findFirst();

        if (recipeOpt.isEmpty()) {
            return;
        }

        // 明确类型
        CompactingRecipe foundRecipe = recipeOpt.get();

        // --- 错误2 修正 ---
        List<ItemStack> ingredientsForParticles = new ArrayList<>();
        IItemHandlerModifiable inputInvForParticles = basin.getInputInventory();
        for (int i = 0; i < inputInvForParticles.getSlots(); i++) {
            ItemStack stackInSlot = inputInvForParticles.getStackInSlot(i);
            if (!stackInSlot.isEmpty()) {
                ingredientsForParticles.add(stackInSlot.copy()); // 复制以防万一
            }
        }

        IItemHandlerModifiable inputInv = basin.getInputInventory();
        // 遍历配方所需的原料
        for (Ingredient ingredient : foundRecipe.getIngredients()) {
            boolean ingredientMet = false;
            // 在盆的输入槽中寻找匹配的物品
            for (int i = 0; i < inputInv.getSlots(); i++) {
                if (ingredient.test(inputInv.getStackInSlot(i))) {
                    inputInv.extractItem(i, 1, false); // 找到后扣除1个
                    ingredientMet = true;
                    break;
                }
            }
            if (!ingredientMet) {
                // 如果找不到任何一个原料，这是一个意外情况，中止操作
                // 理论上 BasinRecipe.match 已经保证了原料充足
                return;
            }
        }

        IItemHandlerModifiable outputInv = basin.getOutputInventory();
        ItemStack result = foundRecipe.getResultItem(level.registryAccess());
        ItemHandlerHelper.insertItemStacked(outputInv, result, false);

        BlockPos pos = basin.getBlockPos();
        level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0F, 1.0F);

        // 注意：粒子效果在盆的下方生成，所以传递 pos.below()
        ModMessages.sendToClients(new BasinProcessingPacket(pos.below(), ingredientsForParticles));

        player.swing(hand);
        player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        basin.notifyUpdate();
    }
}