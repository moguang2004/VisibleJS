package com.moguang.visiblejs;

import com.moguang.visiblejs.menu.RecipeCreatorMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.moguang.visiblejs.VisibleJS.MODID;

public class VisibleMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final RegistryObject<MenuType<RecipeCreatorMenu>> RECIPE_CREATOR_MENU = MENU_TYPES.register("recipe_creator_menu",
            () -> new MenuType<>(RecipeCreatorMenu::new, FeatureFlags.VANILLA_SET));

}
