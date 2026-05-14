package com.moguang.visiblejs.client.screen;

import com.moguang.visiblejs.common.recipe.RecipeCategoryInfo;
import com.moguang.visiblejs.menu.RecipeCreatorMenu;
import com.moguang.visiblejs.network.VisibleJSNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class RecipeCreatorScreen extends AbstractContainerScreen<RecipeCreatorMenu> {
    private static final java.lang.reflect.Field SLOT_X_FIELD;
    private static final java.lang.reflect.Field SLOT_Y_FIELD;

    static {
        try {
            SLOT_X_FIELD = Slot.class.getDeclaredField("f_40220_");
            SLOT_Y_FIELD = Slot.class.getDeclaredField("f_40221_");
            SLOT_X_FIELD.setAccessible(true);
            SLOT_Y_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize reflection for Slot", e);
        }
    }

    private static final int TAB_WIDTH = 22;
    private static final int TAB_HEIGHT = 20;
    private static final int TAB_Y_OFFSET = -18;
    private static final int TABS_PER_PAGE = 6;
    private static final int NAV_WIDTH = 12;
    private static final int NAV_HEIGHT = 12;
    private static final int TAB_START_X = 20;
    private static final int TAB_BORDER = 0xFF171717;
    private static final int TAB_NORMAL = 0xFF2A2F36;
    private static final int TAB_HOVER = 0xFF3A4652;
    private static final int TAB_SELECTED = 0xFF5C7EA3;

    private int currentPage;

    public RecipeCreatorScreen(RecipeCreatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        // Generate button
        this.addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.translatable("gui.visiblejs.recipe_creator.generate"), button -> VisibleJSNetwork.sendGenerateRecipeRequest(this.menu.getRecipeType()))
                .bounds(this.leftPos + 98, this.topPos + 59, 64, 20)
                .build());
        
        syncPageToSelection();
        // Initialize slot positions
        updateSlotPositions();
    }

    private void syncPageToSelection() {
        List<RecipeCategoryInfo> categories = RecipeCategoryInfo.all();
        if (categories.isEmpty()) {
            this.currentPage = 0;
            return;
        }

        int selectedIndex = findSelectedIndex(categories);
        if (selectedIndex < 0) {
            this.menu.setRecipeType(categories.get(0).recipeType());
            selectedIndex = 0;
        }

        this.currentPage = selectedIndex / TABS_PER_PAGE;
    }

    private void updateSlotPositions() {
        RecipeCategoryInfo categoryInfo = this.menu.getCategoryInfo();

        for (int i = 0; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.slots.get(i);
            RecipeCategoryInfo.SlotPosition pos = categoryInfo.slotPosition(i);
            try {
                SLOT_X_FIELD.set(slot, pos.x());
                SLOT_Y_FIELD.set(slot, pos.y());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to update slot position", e);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RecipeCategoryInfo categoryInfo = this.menu.getCategoryInfo();
        graphics.blit(categoryInfo.backgroundTexture(), this.leftPos, this.topPos, 0, 0, categoryInfo.backgroundWidth(), categoryInfo.backgroundHeight());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        RecipeCategoryInfo categoryInfo = this.menu.getCategoryInfo();
        Component typeName = categoryInfo.title();
        int textWidth = this.font.width(typeName);
        int textX = (this.imageWidth - textWidth) / 2;
        graphics.drawString(this.font, typeName, textX, 6, 0x404040, false);

        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTabs(graphics, mouseX, mouseY);
        renderHoveredTabTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0 && handlePageClick(mouseX, mouseY)) {
            return true;
        }
        if (mouseButton == 0 && handleTabClick(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void renderTabs(GuiGraphics graphics, int mouseX, int mouseY) {
        List<RecipeCategoryInfo> categories = RecipeCategoryInfo.all();
        int pageCount = getPageCount(categories);
        this.currentPage = Math.min(this.currentPage, Math.max(pageCount - 1, 0));

        int tabY = this.topPos + TAB_Y_OFFSET;
        int navY = tabY + 4;
        int leftNavX = this.leftPos + 4;
        int rightNavX = this.leftPos + this.imageWidth - NAV_WIDTH - 4;

        renderNavButton(graphics, leftNavX, navY, mouseX, mouseY, pageCount > 1, "<");
        renderNavButton(graphics, rightNavX, navY, mouseX, mouseY, pageCount > 1, ">");

        int startIndex = this.currentPage * TABS_PER_PAGE;
        int endIndex = Math.min(startIndex + TABS_PER_PAGE, categories.size());

        for (int index = startIndex; index < endIndex; ++index) {
            RecipeCategoryInfo categoryInfo = categories.get(index);
            int tabX = this.leftPos + TAB_START_X + (index - startIndex) * TAB_WIDTH;
            boolean selected = categoryInfo.recipeType() == this.menu.getRecipeType();
            boolean hovered = isMouseOverTab(tabX, tabY, mouseX, mouseY);

            graphics.fill(tabX, tabY, tabX + TAB_WIDTH, tabY + TAB_HEIGHT, TAB_BORDER);
            graphics.fill(tabX + 1, tabY + 1, tabX + TAB_WIDTH - 1, tabY + TAB_HEIGHT - 1, selected ? TAB_SELECTED : hovered ? TAB_HOVER : TAB_NORMAL);

            ItemStack icon = categoryInfo.icon();
            graphics.renderItem(icon, tabX + 3, tabY + 2);

            if (selected) {
                graphics.fill(tabX + 2, tabY + TAB_HEIGHT - 2, tabX + TAB_WIDTH - 2, tabY + TAB_HEIGHT - 1, 0xFFFFFFFF);
            }
        }
    }

    private void renderHoveredTabTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        RecipeCategoryInfo hovered = getHoveredTab(mouseX, mouseY);
        if (hovered != null) {
            graphics.renderTooltip(this.font, hovered.title(), mouseX, mouseY);
        }
    }

    private void renderNavButton(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, boolean enabled, String label) {
        boolean hovered = enabled && mouseX >= x && mouseX < x + NAV_WIDTH && mouseY >= y && mouseY < y + NAV_HEIGHT;
        int background = enabled ? (hovered ? TAB_HOVER : TAB_NORMAL) : 0xFF1B1B1B;
        graphics.fill(x, y, x + NAV_WIDTH, y + NAV_HEIGHT, TAB_BORDER);
        graphics.fill(x + 1, y + 1, x + NAV_WIDTH - 1, y + NAV_HEIGHT - 1, background);
        graphics.drawCenteredString(this.font, label, x + NAV_WIDTH / 2, y + 2, enabled ? 0xFFFFFF : 0x777777);
    }

    private boolean handlePageClick(double mouseX, double mouseY) {
        List<RecipeCategoryInfo> categories = RecipeCategoryInfo.all();
        int pageCount = getPageCount(categories);
        if (pageCount <= 1) {
            return false;
        }

        int tabY = this.topPos + TAB_Y_OFFSET + 4;
        int leftNavX = this.leftPos + 4;
        int rightNavX = this.leftPos + this.imageWidth - NAV_WIDTH - 4;

        if (isMouseOverArrow(leftNavX, tabY, mouseX, mouseY)) {
            this.currentPage = (this.currentPage - 1 + pageCount) % pageCount;
            return true;
        }

        if (isMouseOverArrow(rightNavX, tabY, mouseX, mouseY)) {
            this.currentPage = (this.currentPage + 1) % pageCount;
            return true;
        }

        return false;
    }

    private boolean handleTabClick(double mouseX, double mouseY) {
        RecipeCategoryInfo hovered = getHoveredTab(mouseX, mouseY);
        if (hovered == null) {
            return false;
        }

        this.menu.setRecipeType(hovered.recipeType());
        syncPageToSelection();
        updateSlotPositions();
        return true;
    }

    private RecipeCategoryInfo getHoveredTab(double mouseX, double mouseY) {
        List<RecipeCategoryInfo> categories = RecipeCategoryInfo.all();
        int tabY = this.topPos + TAB_Y_OFFSET;
        int startIndex = this.currentPage * TABS_PER_PAGE;
        int endIndex = Math.min(startIndex + TABS_PER_PAGE, categories.size());

        for (int index = startIndex; index < endIndex; ++index) {
            int tabX = this.leftPos + TAB_START_X + (index - startIndex) * TAB_WIDTH;
            if (isMouseOverTab(tabX, tabY, mouseX, mouseY)) {
                return categories.get(index);
            }
        }

        return null;
    }

    private int findSelectedIndex(List<RecipeCategoryInfo> categories) {
        for (int index = 0; index < categories.size(); ++index) {
            if (categories.get(index).recipeType() == this.menu.getRecipeType()) {
                return index;
            }
        }
        return -1;
    }

    private int getPageCount(List<RecipeCategoryInfo> categories) {
        return Math.max(1, (categories.size() + TABS_PER_PAGE - 1) / TABS_PER_PAGE);
    }

    private boolean isMouseOverTab(int tabX, int tabY, double mouseX, double mouseY) {
        return mouseX >= tabX && mouseX < tabX + TAB_WIDTH && mouseY >= tabY && mouseY < tabY + TAB_HEIGHT;
    }

    private boolean isMouseOverArrow(int tabX, int tabY, double mouseX, double mouseY) {
        return mouseX >= tabX && mouseX < tabX + NAV_WIDTH && mouseY >= tabY && mouseY < tabY + NAV_HEIGHT;
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        int slotIndex = this.menu.slots.indexOf(slot);
        RecipeCategoryInfo categoryInfo = this.menu.getCategoryInfo();
        if (!categoryInfo.isSlotVisible(slotIndex)) {
            return; // Ignore clicks on hidden slots
        }
        super.slotClicked(slot, slotId, mouseButton, type);
    }
}
