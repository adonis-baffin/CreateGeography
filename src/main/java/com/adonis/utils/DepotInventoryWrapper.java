package com.adonis.utils;

import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

// 这个类是为了模拟Create内部的私有DepotInventory，以便RecipeWrapper可以正确工作
public class DepotInventoryWrapper extends ItemStackHandler {

    private final DepotBehaviour behaviour;

    public DepotInventoryWrapper(DepotBehaviour behaviour) {
        super(1);
        this.behaviour = behaviour;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? behaviour.getHeldItemStack() : ItemStack.EMPTY;
    }
}