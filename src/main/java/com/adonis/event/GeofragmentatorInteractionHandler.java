package com.adonis.event;

import com.adonis.CreateGeography;
import com.adonis.content.item.GeofragmentatorItem;
import com.adonis.networking.ModMessages;
import com.adonis.networking.packet.BasinProcessingPacket;
import com.adonis.networking.packet.BeltProcessingPacket;
import com.adonis.networking.packet.DepotProcessingPacket;
import com.adonis.utils.DepotInventoryWrapper;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
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
            return; // 已处理，直接返回
        }

        // 尝试获取 BasinBlockEntity
        if (level.getBlockEntity(pos) instanceof BasinBlockEntity basin) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            if (!level.isClientSide) {
                handleBasinInteraction(player, event.getHand(), basin);
            }
        }

        // 尝试处理传送带
        if (level.getBlockEntity(pos) instanceof BeltBlockEntity belt) {
            // 我们需要玩家的视线来确定目标物品
            HitResult hitResult = event.getHitVec();
            if (handleBeltInteraction(player, event.getHand(), belt, hitResult.getLocation())) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
            return;
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

        Optional<CompactingRecipe> recipeOpt = compactingRecipes.stream()
                .filter(recipe -> BasinRecipe.match(basin, recipe))
                .findFirst();

        if (recipeOpt.isEmpty()) {
            return;
        }

        CompactingRecipe foundRecipe = recipeOpt.get();

        // 在应用配方前，先收集粒子信息
        List<ItemStack> ingredientsForParticles = new ArrayList<>();
        IItemHandlerModifiable inputInvForParticles = basin.getInputInventory();
        for (int i = 0; i < inputInvForParticles.getSlots(); i++) {
            ItemStack stackInSlot = inputInvForParticles.getStackInSlot(i);
            if (!stackInSlot.isEmpty()) {
                ingredientsForParticles.add(stackInSlot.copy());
            }
        }

        // 使用Create的API来安全地应用配方，它会处理所有物品和流体的消耗与产出
        boolean success = BasinRecipe.apply(basin, foundRecipe);

        // 如果API调用成功，才执行后续效果
        if (success) {
            BlockPos pos = basin.getBlockPos();
            level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0F, 1.0F);

            // 发送粒子效果数据包
            ModMessages.sendToClients(new BasinProcessingPacket(pos.below(), ingredientsForParticles));

            player.swing(hand);
            player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));

            // apply方法内部已经修改了inventory，它会自动触发 notifyUpdate，但我们再调用一次以确保万无一失
            basin.notifyUpdate();
        }
    }

    private static boolean handleBeltInteraction(Player player, InteractionHand hand, BeltBlockEntity belt, Vec3 hitLocation) {
        Level level = belt.getLevel();
        if (level == null || level.isClientSide()) {
            return true; // 客户端直接成功，以阻止默认交互
        }

        BeltBlockEntity controller = belt.getControllerBE();
        if (controller == null) return false;

        BeltInventory inventory = controller.getInventory();
        if (inventory == null) return false;

        final TransportedItemStack[] closestItem = {null};
        final double[] minDistance = {Double.MAX_VALUE};

        for (TransportedItemStack transported : inventory.getTransportedItems()) {
            Vec3 itemPos = BeltHelper.getVectorForOffset(controller, transported.beltPosition);
            double distance = itemPos.distanceToSqr(hitLocation);
            if (distance < minDistance[0] && distance < 1.0) {
                minDistance[0] = distance;
                closestItem[0] = transported;
            }
        }

        if (closestItem[0] == null) {
            return false;
        }

        TransportedItemStack target = closestItem[0];

        // --- 关键修正点 ---
        // 在对 `target.stack` 做任何修改之前，先为粒子效果创建一个副本
        ItemStack particleStack = target.stack.copy();

        ItemStackHandler tempInv = new ItemStackHandler(1);
        tempInv.setStackInSlot(0, target.stack);
        RecipeWrapper wrapper = new RecipeWrapper(tempInv);
        Optional<PressingRecipe> recipeOpt = AllRecipeTypes.PRESSING.find(wrapper, level);

        if (recipeOpt.isEmpty()) {
            return false;
        }

        Recipe<RecipeWrapper> recipe = recipeOpt.get();

        boolean processEntireStack = player.isShiftKeyDown();
        int amountToProcess = processEntireStack ? target.stack.getCount() : 1;

        ItemStack inputForRecipe = ItemHandlerHelper.copyStackWithSize(target.stack, amountToProcess);
        List<ItemStack> results = RecipeApplier.applyRecipeOn(level, inputForRecipe, recipe);

        if (results.isEmpty()) {
            return false;
        }

        // --- 执行替换 ---
        // 1. 从传送带移除旧物品
        target.stack.shrink(amountToProcess);

        // 2. 将新物品添加到传送带
        for (ItemStack resultStack : results) {
            TransportedItemStack newTIS = new TransportedItemStack(resultStack);
            newTIS.beltPosition = target.beltPosition;
            newTIS.sideOffset = target.sideOffset;
            newTIS.angle = target.angle;
            newTIS.insertedFrom = target.insertedFrom;
            inventory.addItem(newTIS);
        }

        // BeltInventory的tick()方法会自动清理空的TransportedItemStack
        // 所以我们不需要手动移除

        controller.notifyUpdate();

        // --- 视觉与听觉效果 ---
        Vec3 particlePos = BeltHelper.getVectorForOffset(controller, target.beltPosition);
        level.playSound(null, particlePos.x, particlePos.y, particlePos.z, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, 1.5F);

        // 使用我们之前创建的副本来发送粒子包
        ModMessages.sendToClients(new BeltProcessingPacket(particlePos.add(0, 0.25, 0), particleStack));

        player.swing(hand);
        player.getItemInHand(hand).hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));

        return true;
    }

}