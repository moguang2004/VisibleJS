package com.moguang.visiblejs.client;

import com.moguang.visiblejs.VisibleJS;
import com.moguang.visiblejs.VisibleMenuTypes;
import com.moguang.visiblejs.client.screen.RecipeCreatorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = VisibleJS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class VisibleJSClientEvents {
    private VisibleJSClientEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(VisibleMenuTypes.RECIPE_CREATOR_MENU.get(), RecipeCreatorScreen::new));
    }
}

