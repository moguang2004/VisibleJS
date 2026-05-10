package com.moguang.visiblejs.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

@FunctionalInterface
public interface MenuFactory {
    AbstractContainerMenu create(int containerId, Inventory playerInventory, ContainerLevelAccess access);
}
