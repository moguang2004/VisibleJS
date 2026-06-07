package com.moguang.visiblejs;

import com.moguang.visiblejs.common.Item.ToolItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.moguang.visiblejs.VisibleJS.*;

public class VisibleJSItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<ToolItem> RECIPE_CREATOR = ITEMS.register("recipe_creator", () -> new ToolItem(new Item.Properties()));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> RECIPE_CREATOR.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(RECIPE_CREATOR.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());
}
