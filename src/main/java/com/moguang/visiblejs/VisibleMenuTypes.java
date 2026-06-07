package com.moguang.visiblejs;

import net.minecraft.core.registries.Registries;
import com.moguang.visiblejs.menu.RecipeCreatorMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.moguang.visiblejs.VisibleJS.MODID;

public class VisibleMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<RecipeCreatorMenu>> RECIPE_CREATOR_MENU = MENU_TYPES.register("recipe_creator_menu",
            () -> new MenuType<>(RecipeCreatorMenu::new, FeatureFlags.VANILLA_SET));

}
