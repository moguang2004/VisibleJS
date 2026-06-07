package com.moguang.visiblejs.client;

import com.moguang.visiblejs.VisibleJS;
import com.moguang.visiblejs.VisibleMenuTypes;
import com.moguang.visiblejs.client.screen.RecipeCreatorScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = VisibleJS.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class VisibleJSClientEvents {
    private VisibleJSClientEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(VisibleMenuTypes.RECIPE_CREATOR_MENU.get(), RecipeCreatorScreen::new);
    }
}

