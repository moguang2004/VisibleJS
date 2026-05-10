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
        buffer.writeInt(message.recipeType.ordinal());
    }

    public static RecipeCreatorGeneratePacket decode(FriendlyByteBuf buffer) {
        int ordinal = buffer.readInt();
        return new RecipeCreatorGeneratePacket(RecipeType.byOrdinal(ordinal));
    }

    public static void handle(RecipeCreatorGeneratePacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            if (!(player.containerMenu instanceof RecipeCreatorMenu menu)) {
                player.sendSystemMessage(Component.literal("[VisibleJS] 请先打开 recipe_creator 界面再生成脚本。"));
                return;
            }

            try {
                String script = RecipeScriptGenerator.generateRecipe(menu, message.recipeType);
                String filename = getFilenameForType(message.recipeType);
                
                // 写入到 kubejs/server_scripts/下的对应文件
                Path gameDir = FMLPaths.GAMEDIR.get();
                Path scriptDir = gameDir.resolve("kubejs/server_scripts");
                Files.createDirectories(scriptDir);
                Path scriptFile = scriptDir.resolve(filename);
                
                boolean isNewFile = !Files.exists(scriptFile);
                String content = script + "\n\n";
                Files.writeString(scriptFile, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                
                LOGGER.info("Generated KJS script:\n{}", script);
                player.sendSystemMessage(Component.literal("[VisibleJS] 已生成 KJS 脚本" + (isNewFile ? "并新建" : "并追加到") + " kubejs/server_scripts/" + filename));
                for (String line : script.split("\\R")) {
                    player.sendSystemMessage(Component.literal(line));
                }
            } catch (IllegalStateException exception) {
                player.sendSystemMessage(Component.literal("[VisibleJS] " + exception.getMessage()));
            } catch (IOException exception) {
                player.sendSystemMessage(Component.literal("[VisibleJS] 保存文件失败: " + exception.getMessage()));
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
            default:
                return "recipes.js";
        }
    }
}
