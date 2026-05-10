package com.moguang.visiblejs.network;

import com.moguang.visiblejs.VisibleJS;
import com.moguang.visiblejs.common.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class VisibleJSNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(VisibleJS.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static boolean registered;

    private VisibleJSNetwork() {
    }

    public static void init() {
        if (registered) {
            return;
        }

        CHANNEL.messageBuilder(RecipeCreatorGeneratePacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RecipeCreatorGeneratePacket::encode)
                .decoder(RecipeCreatorGeneratePacket::decode)
                .consumerMainThread(RecipeCreatorGeneratePacket::handle)
                .add();
        registered = true;
    }

    public static void sendGenerateRecipeRequest(RecipeType type) {
        CHANNEL.sendToServer(new RecipeCreatorGeneratePacket(type));
    }
}



