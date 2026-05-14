package com.moguang.visiblejs.network;

import com.mojang.logging.LogUtils;
import com.moguang.visiblejs.common.recipe.RecipeType;
import com.moguang.visiblejs.common.script.RecipeScriptGenerator;
import com.moguang.visiblejs.menu.RecipeCreatorMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

public final class RecipeCreatorGeneratePacket {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final RecipeType recipeType;

    public RecipeCreatorGeneratePacket(RecipeType recipeType) {
        this.recipeType = recipeType;
    }

    public static void encode(RecipeCreatorGeneratePacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.recipeType.getId());
    }

    public static RecipeCreatorGeneratePacket decode(FriendlyByteBuf buffer) {
        return new RecipeCreatorGeneratePacket(RecipeType.byId(buffer.readUtf(32767)));
    }

    public static void handle(RecipeCreatorGeneratePacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            if (!(player.containerMenu instanceof RecipeCreatorMenu menu)) {
                player.sendSystemMessage(Component.literal("[VisibleJS] ").append(Component.translatable("message.visiblejs.open_gui_first")));
                return;
            }

            if (!message.recipeType.isAvailable()) {
                player.sendSystemMessage(Component.literal("[VisibleJS] ").append(Component.translatable("message.visiblejs.error.recipe_type_unavailable", message.recipeType.getDisplayComponent())));
                return;
            }

            try {
                String script = RecipeScriptGenerator.generateRecipe(menu, message.recipeType);
                String filename = getFilenameForType(message.recipeType);
                
                // Write to kubejs/server_scripts/
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
        });
        context.setPacketHandled(true);
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
