package com.moguang.visiblejs.common.recipe;

import net.minecraft.network.chat.Component;

public enum RecipeType {
    SHAPED("shaped", true),
    SHAPELESS("shapeless", true),
    SMELTING("smelting", false),
    BLASTING("blasting", false),
    SMOKING("smoking", false),
    CAMPFIRE_COOKING("campfire_cooking", false),
    SMITHING("smithing", false),
    STONECUTTING("stonecutting", false);

    private final String id;
    private final boolean usesCraftingGrid;

    RecipeType(String id, boolean usesCraftingGrid) {
        this.id = id;
        this.usesCraftingGrid = usesCraftingGrid;
    }

    public String getId() {
        return id;
    }

    public String getTranslationKey() {
        return "recipe_type.visiblejs." + id;
    }

    public boolean usesCraftingGrid() {
        return usesCraftingGrid;
    }

    public Component getDisplayComponent() {
        return Component.translatable(getTranslationKey());
    }

    public static RecipeType byOrdinal(int ordinal) {
        RecipeType[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return SHAPED;
        }
        return values[ordinal];
    }
}
