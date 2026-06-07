package com.moguang.visiblejs.network;

import com.moguang.visiblejs.VisibleJS;
import com.moguang.visiblejs.common.recipe.RecipeType;
import com.moguang.visiblejs.common.script.RecipeScriptGenerator;
import com.moguang.visiblejs.menu.RecipeCreatorMenu;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public record RecipeCreatorGeneratePacket(RecipeType recipeType) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<RecipeCreatorGeneratePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(VisibleJS.MODID, "generate_recipe"));
    public static final StreamCodec<ByteBuf, RecipeCreatorGeneratePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            packet -> packet.recipeType().getId(),
            id -> new RecipeCreatorGeneratePacket(RecipeType.byId(id))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RecipeCreatorGeneratePacket message, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.containerMenu instanceof RecipeCreatorMenu menu)) {
            player.sendSystemMessage(Component.literal("[VisibleJS] ").append(Component.translatable("message.visiblejs.open_gui_first")));
            return;
        }

        if (!message.recipeType().isAvailable()) {
            player.sendSystemMessage(Component.literal("[VisibleJS] ").append(Component.translatable("message.visiblejs.error.recipe_type_unavailable", message.recipeType().getDisplayComponent())));
            return;
        }

        try {
            String script = RecipeScriptGenerator.generateRecipe(menu, message.recipeType());
            String filename = getFilenameForType(message.recipeType());

            Path gameDir = FMLPaths.GAMEDIR.get();
            Path scriptDir = gameDir.resolve("kubejs/server_scripts");
            Files.createDirectories(scriptDir);
            Path scriptFile = scriptDir.resolve(filename);

            boolean isNewFile = !Files.exists(scriptFile);
            String content = script + "\n\n";
            Files.writeString(scriptFile, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            LOGGER.info("Generated KJS script:\n{}", script);
            Component action = Component.translatable(isNewFile ? "message.visiblejs.new_file" : "message.visiblejs.appended");
            player.sendSystemMessage(Component.literal("[VisibleJS] ").append(Component.translatable("message.visiblejs.generated", action, filename)));
            for (String line : script.split("\\R")) {
                player.sendSystemMessage(Component.literal(line));
            }
        } catch (IllegalStateException exception) {
            player.sendSystemMessage(Component.literal("[VisibleJS] " + exception.getMessage()));
        } catch (IOException exception) {
            player.sendSystemMessage(Component.literal("[VisibleJS] ").append(Component.translatable("message.visiblejs.save_failed", exception.getMessage())));
            LOGGER.error("Failed to write KJS script to file", exception);
        }
    }

    private static String getFilenameForType(RecipeType type) {
        switch (type) {
            case SHAPED:
            case SHAPELESS:
                return "crafting.js";
            case SMELTING:
            case BLASTING:
            case SMOKING:
            case CAMPFIRE_COOKING:
                return "smelting.js";
            case SMITHING:
                return "smithing.js";
            case STONECUTTING:
                return "stonecutting.js";
            case CREATE_CRUSHING:
            case CREATE_MILLING:
            case CREATE_PRESSING:
            case CREATE_CUTTING:
            case CREATE_SANDPAPER_POLISHING:
            case CREATE_HAUNTING:
            case CREATE_SPLASHING:
            case CREATE_EMPTYING:
            case CREATE_WASHING:
            case CREATE_DEPLOYING:
            case CREATE_MIXING:
            case CREATE_COMPACTING:
                return "create.js";
            default:
                return "recipes.js";
        }
    }
}
