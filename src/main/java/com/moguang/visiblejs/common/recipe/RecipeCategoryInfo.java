package com.moguang.visiblejs.common.recipe;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class RecipeCategoryInfo {
    private static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
    private static final ResourceLocation FURNACE_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");
    private static final ResourceLocation BLAST_FURNACE_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/blast_furnace.png");
    private static final ResourceLocation SMOKER_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/smoker.png");
    private static final ResourceLocation SMITHING_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/smithing.png");
    private static final ResourceLocation STONECUTTER_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/stonecutter.png");
    private static final ResourceLocation CREATE_SINGLE_TEXTURE = FURNACE_TEXTURE;
    private static final ResourceLocation CREATE_MULTI_TEXTURE = CRAFTING_TEXTURE;
    private static final ResourceLocation CREATE_DEPLOY_TEXTURE = SMITHING_TEXTURE;

    private static final Map<RecipeType, RecipeCategoryInfo> CACHE = new EnumMap<>(RecipeType.class);

    static {
        CACHE.put(RecipeType.SHAPED, new RecipeCategoryInfo(RecipeType.SHAPED, new ItemStack(Blocks.CRAFTING_TABLE), CRAFTING_TEXTURE, 176, 166));
        CACHE.put(RecipeType.SHAPELESS, new RecipeCategoryInfo(RecipeType.SHAPELESS, new ItemStack(Blocks.CRAFTING_TABLE), CRAFTING_TEXTURE, 176, 166));
        CACHE.put(RecipeType.SMELTING, new RecipeCategoryInfo(RecipeType.SMELTING, new ItemStack(Blocks.FURNACE), FURNACE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.BLASTING, new RecipeCategoryInfo(RecipeType.BLASTING, new ItemStack(Blocks.BLAST_FURNACE), BLAST_FURNACE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.SMOKING, new RecipeCategoryInfo(RecipeType.SMOKING, new ItemStack(Blocks.SMOKER), SMOKER_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CAMPFIRE_COOKING, new RecipeCategoryInfo(RecipeType.CAMPFIRE_COOKING, new ItemStack(Items.CAMPFIRE), FURNACE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.SMITHING, new RecipeCategoryInfo(RecipeType.SMITHING, new ItemStack(Blocks.SMITHING_TABLE), SMITHING_TEXTURE, 176, 166));
        CACHE.put(RecipeType.STONECUTTING, new RecipeCategoryInfo(RecipeType.STONECUTTING, new ItemStack(Blocks.STONECUTTER), STONECUTTER_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_CRUSHING, new RecipeCategoryInfo(RecipeType.CREATE_CRUSHING, new ItemStack(Blocks.COBBLESTONE), CREATE_SINGLE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_MILLING, new RecipeCategoryInfo(RecipeType.CREATE_MILLING, new ItemStack(Items.WHEAT), CREATE_SINGLE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_PRESSING, new RecipeCategoryInfo(RecipeType.CREATE_PRESSING, new ItemStack(Items.IRON_INGOT), CREATE_SINGLE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_CUTTING, new RecipeCategoryInfo(RecipeType.CREATE_CUTTING, new ItemStack(Items.SHEARS), CREATE_SINGLE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_SANDPAPER_POLISHING, new RecipeCategoryInfo(RecipeType.CREATE_SANDPAPER_POLISHING, new ItemStack(Items.PAPER), CREATE_SINGLE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_HAUNTING, new RecipeCategoryInfo(RecipeType.CREATE_HAUNTING, new ItemStack(Items.ECHO_SHARD), CREATE_SINGLE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_SPLASHING, new RecipeCategoryInfo(RecipeType.CREATE_SPLASHING, new ItemStack(Items.SPLASH_POTION), CREATE_SINGLE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_EMPTYING, new RecipeCategoryInfo(RecipeType.CREATE_EMPTYING, new ItemStack(Items.BUCKET), CREATE_SINGLE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_WASHING, new RecipeCategoryInfo(RecipeType.CREATE_WASHING, new ItemStack(Items.WATER_BUCKET), CREATE_SINGLE_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_DEPLOYING, new RecipeCategoryInfo(RecipeType.CREATE_DEPLOYING, new ItemStack(Items.DISPENSER), CREATE_DEPLOY_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_MIXING, new RecipeCategoryInfo(RecipeType.CREATE_MIXING, new ItemStack(Items.BOWL), CREATE_MULTI_TEXTURE, 176, 166));
        CACHE.put(RecipeType.CREATE_COMPACTING, new RecipeCategoryInfo(RecipeType.CREATE_COMPACTING, new ItemStack(Blocks.HONEY_BLOCK), CREATE_MULTI_TEXTURE, 176, 166));
    }

    public record SlotPosition(int x, int y) {
    }

    private final RecipeType recipeType;
    private final ItemStack icon;
    private final ResourceLocation backgroundTexture;
    private final int backgroundWidth;
    private final int backgroundHeight;

    private RecipeCategoryInfo(RecipeType recipeType, ItemStack icon, ResourceLocation backgroundTexture, int backgroundWidth, int backgroundHeight) {
        this.recipeType = recipeType;
        this.icon = icon.copy();
        this.backgroundTexture = backgroundTexture;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
    }

    public static RecipeCategoryInfo of(RecipeType type) {
        return CACHE.getOrDefault(type, CACHE.get(RecipeType.SHAPED));
    }

    public static List<RecipeCategoryInfo> all() {
        return Arrays.stream(RecipeType.visibleValues().toArray(new RecipeType[0])).map(RecipeCategoryInfo::of).toList();
    }

    public RecipeType recipeType() {
        return recipeType;
    }

    public Component title() {
        return recipeType.getDisplayComponent();
    }

    public ItemStack icon() {
        return icon.copy();
    }

    public ResourceLocation backgroundTexture() {
        return backgroundTexture;
    }

    public int backgroundWidth() {
        return backgroundWidth;
    }

    public int backgroundHeight() {
        return backgroundHeight;
    }

    public SlotPosition slotPosition(int slotIndex) {
        if (slotIndex >= 10) {
            return getInventorySlotPosition(slotIndex);
        }

        return switch (recipeType) {
            case SHAPED, SHAPELESS -> getCraftingSlotPosition(slotIndex);
            case SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING -> getCookingSlotPosition(slotIndex);
            case SMITHING -> getSmithingSlotPosition(slotIndex);
            case STONECUTTING -> getStonecuttingSlotPosition(slotIndex);
            case CREATE_CRUSHING, CREATE_MILLING, CREATE_PRESSING, CREATE_CUTTING, CREATE_SANDPAPER_POLISHING, CREATE_HAUNTING, CREATE_SPLASHING, CREATE_EMPTYING, CREATE_WASHING -> getCookingSlotPosition(slotIndex);
            case CREATE_DEPLOYING -> getDeployingSlotPosition(slotIndex);
            case CREATE_MIXING, CREATE_COMPACTING -> getCraftingSlotPosition(slotIndex);
        };
    }

    public boolean isSlotVisible(int slotIndex) {
        SlotPosition position = slotPosition(slotIndex);
        return position.x() >= 0 && position.y() >= 0;
    }

    private static SlotPosition getInventorySlotPosition(int slotIndex) {
        if (slotIndex >= 10 && slotIndex < 37) {
            int playerIndex = slotIndex - 10;
            int row = playerIndex / 9;
            int column = playerIndex % 9;
            return new SlotPosition(8 + column * 18, 84 + row * 18);
        }

        if (slotIndex >= 37 && slotIndex < 46) {
            int column = slotIndex - 37;
            return new SlotPosition(8 + column * 18, 142);
        }

        return hidden();
    }

    private static SlotPosition getCraftingSlotPosition(int slotIndex) {
        return switch (slotIndex) {
            case 0 -> new SlotPosition(124, 35);
            case 1 -> new SlotPosition(30, 17);
            case 2 -> new SlotPosition(48, 17);
            case 3 -> new SlotPosition(66, 17);
            case 4 -> new SlotPosition(30, 35);
            case 5 -> new SlotPosition(48, 35);
            case 6 -> new SlotPosition(66, 35);
            case 7 -> new SlotPosition(30, 53);
            case 8 -> new SlotPosition(48, 53);
            case 9 -> new SlotPosition(66, 53);
            default -> hidden();
        };
    }

    private static SlotPosition getCookingSlotPosition(int slotIndex) {
        return switch (slotIndex) {
            case 0 -> new SlotPosition(116, 35);
            case 5 -> new SlotPosition(56, 17);
            default -> hidden();
        };
    }

    private static SlotPosition getSmithingSlotPosition(int slotIndex) {
        return switch (slotIndex) {
            case 0 -> new SlotPosition(98, 48);
            case 1 -> new SlotPosition(8, 48);
            case 2 -> new SlotPosition(26, 48);
            case 3 -> new SlotPosition(44, 48);
            default -> hidden();
        };
    }

    private static SlotPosition getStonecuttingSlotPosition(int slotIndex) {
        return switch (slotIndex) {
            case 0 -> new SlotPosition(143, 33);
            case 5 -> new SlotPosition(20, 33);
            default -> hidden();
        };
    }

    private static SlotPosition getDeployingSlotPosition(int slotIndex) {
        return switch (slotIndex) {
            case 0 -> new SlotPosition(98, 48);
            case 1 -> new SlotPosition(8, 48);
            case 2 -> new SlotPosition(26, 48);
            default -> hidden();
        };
    }

    private static SlotPosition hidden() {
        return new SlotPosition(-9999, -9999);
    }
}
