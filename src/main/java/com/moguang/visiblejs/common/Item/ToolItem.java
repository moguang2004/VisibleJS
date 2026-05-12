package com.moguang.visiblejs.common.Item;

import com.moguang.visiblejs.menu.RecipeCreatorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ToolItem extends Item {
    private static final Component CONTAINER_TITLE = Component.translatable("container.visiblejs.recipe_creator");

    public ToolItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        openRecipeCreatorMenu(player);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            openRecipeCreatorMenu(player);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    private void openRecipeCreatorMenu(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            MenuProvider menuProvider = new SimpleMenuProvider((containerId, inventory, ignoredPlayer) -> new RecipeCreatorMenu(containerId, inventory), CONTAINER_TITLE);
            serverPlayer.openMenu(menuProvider);
        }
    }
}
