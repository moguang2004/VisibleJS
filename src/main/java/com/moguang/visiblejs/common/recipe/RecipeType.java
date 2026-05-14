package com.moguang.visiblejs.common.recipe;

import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.List;

public enum RecipeType {
    SHAPED("shaped", true),
    SHAPELESS("shapeless", true),
    SMELTING("smelting", false),
    BLASTING("blasting", false),
    SMOKING("smoking", false),
    CAMPFIRE_COOKING("campfire_cooking", false),
    SMITHING("smithing", false),
    STONECUTTING("stonecutting", false),
    CREATE_CRUSHING("create_crushing", false, "create"),
    CREATE_MILLING("create_milling", false, "create"),
    CREATE_PRESSING("create_pressing", false, "create"),
    CREATE_CUTTING("create_cutting", false, "create"),
    CREATE_SANDPAPER_POLISHING("create_sandpaper_polishing", false, "create"),
    CREATE_HAUNTING("create_haunting", false, "create"),
    CREATE_SPLASHING("create_splashing", false, "create"),
    CREATE_EMPTYING("create_emptying", false, "create"),
    CREATE_WASHING("create_washing", false, "create"),
    CREATE_DEPLOYING("create_deploying", false, "create"),
    CREATE_MIXING("create_mixing", true, "create"),
    CREATE_COMPACTING("create_compacting", true, "create");

    private final String id;
    private final boolean usesCraftingGrid;
    private final String requiredModId;

    RecipeType(String id, boolean usesCraftingGrid) {
        this(id, usesCraftingGrid, null);
    }

    RecipeType(String id, boolean usesCraftingGrid, String requiredModId) {
        this.id = id;
        this.usesCraftingGrid = usesCraftingGrid;
        this.requiredModId = requiredModId;
    }

    public String getId() {
        return id;
    }

    public String getRequiredModId() {
        return requiredModId;
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

    public boolean isAvailable() {
        return requiredModId == null || ModList.get().isLoaded(requiredModId);
    }

    public static List<RecipeType> visibleValues() {
        return Arrays.stream(values()).filter(RecipeType::isAvailable).toList();
    }

    public static RecipeType firstVisible() {
        return visibleValues().stream().findFirst().orElse(SHAPED);
    }

    public static RecipeType byId(String id) {
        for (RecipeType value : values()) {
            if (value.id.equals(id)) {
                return value;
            }
        }
        return SHAPED;
    }

    public static RecipeType byOrdinal(int ordinal) {
        RecipeType[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return SHAPED;
        }
        return values[ordinal];
    }
}
