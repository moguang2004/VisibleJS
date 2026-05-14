package com.moguang.visiblejs.menu;

import com.moguang.visiblejs.VisibleMenuTypes;
import com.moguang.visiblejs.common.recipe.RecipeCategoryInfo;
import com.moguang.visiblejs.common.recipe.RecipeType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeCreatorMenu extends AbstractContainerMenu {
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final Player player;
    private RecipeType recipeType = RecipeType.SHAPED;
    private RecipeCategoryInfo categoryInfo = RecipeCategoryInfo.of(recipeType);

    public RecipeCreatorMenu(int containerId, Inventory playerInventory) {
        super(VisibleMenuTypes.RECIPE_CREATOR_MENU.get(), containerId);
        this.player = playerInventory.player;

        this.addSlot(new Slot(this.resultSlots, 0, 124, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }
        });

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 3; ++column) {
                this.addSlot(new Slot(this.craftSlots, column + row * 3, 30 + column * 18, 17 + row * 18));
            }
        }

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public RecipeCategoryInfo getCategoryInfo() {
        return categoryInfo;
    }

    public void setRecipeType(RecipeType type) {
        this.recipeType = type != null && type.isAvailable() ? type : RecipeType.firstVisible();
        this.categoryInfo = RecipeCategoryInfo.of(this.recipeType);
    }

    public void cycleRecipeType(boolean forward) {
        List<RecipeType> values = RecipeType.visibleValues();
        if (values.isEmpty()) {
            setRecipeType(RecipeType.SHAPED);
            return;
        }

        int currentIndex = values.indexOf(this.recipeType);
        if (currentIndex < 0) {
            currentIndex = 0;
        }

        int newIndex;
        if (forward) {
            newIndex = (currentIndex + 1) % values.size();
        } else {
            newIndex = (currentIndex - 1 + values.size()) % values.size();
        }
        setRecipeType(values.get(newIndex));
    }

    public ItemStack getResultStack() {
        return this.resultSlots.getItem(0);
    }

    public List<ItemStack> getIngredientStacks() {
        List<ItemStack> stacks = new ArrayList<>(9);
        for (int index = 0; index < 9; ++index) {
            stacks.add(this.craftSlots.getItem(index));
        }
        return stacks;
    }

    public ItemStack getSingleIngredient() {
        return this.craftSlots.getItem(4); // Center slot for furnace/stonecutting
    }

    public ItemStack getSmithingTemplate() {
        return this.craftSlots.getItem(0); // Top-left for template
    }

    public ItemStack getSmithingBase() {
        return this.craftSlots.getItem(1); // Top-middle for base
    }

    public ItemStack getSmithingAddition() {
        return this.craftSlots.getItem(2); // Top-right for addition
    }

    public List<ItemStack> getShapelessIngredients() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int index = 0; index < 9; ++index) {
            ItemStack stack = this.craftSlots.getItem(index);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    @Override
    public void slotsChanged(Container container) {
        // Recipe creator only needs the raw slot contents.
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack movedStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            movedStack = slotStack.copy();

            if (index < 10) {
                if (!this.moveItemStackTo(slotStack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 1, 10, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == movedStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return movedStack;
    }
}

