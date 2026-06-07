package com.moguang.visiblejs.network;

import com.moguang.visiblejs.common.recipe.RecipeType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class VisibleJSNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private VisibleJSNetwork() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(
                RecipeCreatorGeneratePacket.TYPE,
                RecipeCreatorGeneratePacket.STREAM_CODEC,
                RecipeCreatorGeneratePacket::handle
        );
    }

    public static void sendGenerateRecipeRequest(RecipeType type) {
        PacketDistributor.sendToServer(new RecipeCreatorGeneratePacket(type));
    }
}
