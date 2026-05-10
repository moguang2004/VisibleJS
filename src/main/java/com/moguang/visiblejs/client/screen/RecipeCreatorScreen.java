package com.moguang.visiblejs.client.screen;

import com.moguang.visiblejs.common.recipe.RecipeType;
import com.moguang.visiblejs.menu.RecipeCreatorMenu;
import com.moguang.visiblejs.network.VisibleJSNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class RecipeCreatorScreen extends AbstractContainerScreen<RecipeCreatorMenu> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeCreatorScreen.class);
    
    // Background textures
    private static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
    private static final ResourceLocation FURNACE_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");
    private static final ResourceLocation BLAST_FURNACE_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/blast_furnace.png");
    private static final ResourceLocation SMOKER_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/smoker.png");
    private static final ResourceLocation SMITHING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/smithing.png");
    private static final ResourceLocation STONECUTTER_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/stonecutter.png");
    
    // Reflection fields for modifying Slot positions
    private static final Field SLOT_X_FIELD;
    private static final Field SLOT_Y_FIELD;
    
    static {
        try {
            SLOT_X_FIELD = Slot.class.getDeclaredField("x");
            SLOT_Y_FIELD = Slot.class.getDeclaredField("y");
            SLOT_X_FIELD.setAccessible(true);
            SLOT_Y_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize reflection for Slot", e);
        }
    }

    public RecipeCreatorScreen(RecipeCreatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        // Previous type button
        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
                    this.menu.cycleRecipeType(false);
                    updateSlotPositions();
                })
                .bounds(this.leftPos + 8, this.topPos + 5, 20, 14)
                .build());

        // Next type button
        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
                    this.menu.cycleRecipeType(true);
                    updateSlotPositions();
                })
                .bounds(this.leftPos + 148, this.topPos + 5, 20, 14)
                .build());

        // Generate button
        this.addRenderableWidget(Button.builder(Component.literal("生成脚本"), button -> VisibleJSNetwork.sendGenerateRecipeRequest(this.menu.getRecipeType()))
                .bounds(this.leftPos + 98, this.topPos + 59, 64, 20)
                .build());
        
        // Initialize slot positions
        updateSlotPositions();
    }

    private ResourceLocation getTextureForType(RecipeType type) {
        switch (type) {
            case SHAPED:
            case SHAPELESS:
                return CRAFTING_TEXTURE;
            case SMELTING:
            case CAMPFIRE_COOKING:
                return FURNACE_TEXTURE;
            case BLASTING:
                return BLAST_FURNACE_TEXTURE;
            case SMOKING:
                return SMOKER_TEXTURE;
            case SMITHING:
                return SMITHING_TEXTURE;
            case STONECUTTING:
                return STONECUTTER_TEXTURE;
            default:
                return CRAFTING_TEXTURE;
        }
    }
    
    private void updateSlotPositions() {
        RecipeType type = this.menu.getRecipeType();
        
        for (int i = 0; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.slots.get(i);
            int[] pos = getSlotPositionForType(i, type);
            
            try {
                SLOT_X_FIELD.set(slot, pos[0]);
                SLOT_Y_FIELD.set(slot, pos[1]);
            } catch (Exception e) {
                LOGGER.error("Failed to update slot {} position", i, e);
            }
        }
    }
    
    private int[] getSlotPositionForType(int slotIndex, RecipeType type) {
        // Player inventory slots (index 10+) stay in their original positions
        if (slotIndex >= 10) {
            // Return original positions for inventory slots
            return getOriginalSlotPosition(slotIndex);
        }
        
        switch (type) {
            case SMELTING:
            case BLASTING:
            case SMOKING:
            case CAMPFIRE_COOKING:
                return getFurnaceSlotPosition(slotIndex);
            case SMITHING:
                return getSmithingSlotPosition(slotIndex);
            case STONECUTTING:
                return getStonecutterSlotPosition(slotIndex);
            case SHAPED:
            case SHAPELESS:
            default:
                return getOriginalSlotPosition(slotIndex);
        }
    }
    
    private int[] getOriginalSlotPosition(int slotIndex) {
        switch (slotIndex) {
            case 0: return new int[]{124, 35}; // Result
            case 1: return new int[]{30, 17};  // Craft 0
            case 2: return new int[]{48, 17};  // Craft 1
            case 3: return new int[]{66, 17};  // Craft 2
            case 4: return new int[]{30, 35};  // Craft 3
            case 5: return new int[]{48, 35};  // Craft 4
            case 6: return new int[]{66, 35};  // Craft 5
            case 7: return new int[]{30, 53};  // Craft 6
            case 8: return new int[]{48, 53};  // Craft 7
            case 9: return new int[]{66, 53};  // Craft 8
            default: return new int[]{slotIndex < this.menu.slots.size() ? this.menu.slots.get(slotIndex).x : 0, 
                                     slotIndex < this.menu.slots.size() ? this.menu.slots.get(slotIndex).y : 0};
        }
    }
    
    private int[] getFurnaceSlotPosition(int slotIndex) {
        switch (slotIndex) {
            case 0: return new int[]{116, 35}; // Result
            case 5: return new int[]{56, 17};  // Input (center crafting slot)
            default: return new int[]{-9999, -9999}; // Hide other slots
        }
    }
    
    private int[] getSmithingSlotPosition(int slotIndex) {
        switch (slotIndex) {
            case 0: return new int[]{98, 48}; // Result
            case 1: return new int[]{8, 48};  // Template (craft slot 0)
            case 2: return new int[]{26, 48}; // Base (craft slot 1)
            case 3: return new int[]{44, 48}; // Addition (craft slot 2)
            default: return new int[]{-9999, -9999}; // Hide other slots
        }
    }
    
    private int[] getStonecutterSlotPosition(int slotIndex) {
        switch (slotIndex) {
            case 0: return new int[]{143, 33}; // Result
            case 5: return new int[]{20, 33};  // Input (center crafting slot)
            default: return new int[]{-9999, -9999}; // Hide other slots
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RecipeType type = this.menu.getRecipeType();
        graphics.blit(getTextureForType(type), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        RecipeType type = this.menu.getRecipeType();
        String typeName = type.getDisplayName();
        int textWidth = this.font.width(typeName);
        int textX = (this.imageWidth - textWidth) / 2;
        graphics.drawString(this.font, typeName, textX, 6, 0x404040, false);

        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
    }
    
    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot != null) {
            int slotIndex = this.menu.slots.indexOf(slot);
            RecipeType recipeType = this.menu.getRecipeType();
            int[] pos = getSlotPositionForType(slotIndex, recipeType);
            if (pos[0] < 0 || pos[1] < 0) {
                return; // Ignore clicks on hidden slots
            }
        }
        super.slotClicked(slot, slotId, mouseButton, type);
    }
}
