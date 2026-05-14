package com.moguang.visiblejs.common.script;

import com.moguang.visiblejs.common.recipe.RecipeType;
import com.moguang.visiblejs.menu.RecipeCreatorMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RecipeScriptGenerator {
    private RecipeScriptGenerator() {
    }

    public static String generateRecipe(RecipeCreatorMenu menu, RecipeType type) {
        switch (type) {
            case SHAPED:
                return generateShapedRecipe(menu);
            case SHAPELESS:
                return generateShapelessRecipe(menu);
            case SMELTING:
                return generateCookingRecipe(menu, "smelting");
            case BLASTING:
                return generateCookingRecipe(menu, "blasting");
            case SMOKING:
                return generateCookingRecipe(menu, "smoking");
            case CAMPFIRE_COOKING:
                return generateCookingRecipe(menu, "campfireCooking");
            case SMITHING:
                return generateSmithingRecipe(menu);
            case STONECUTTING:
                return generateStonecuttingRecipe(menu);
            case CREATE_CRUSHING:
                return generateCreateSingleInputRecipe(menu, "crushing");
            case CREATE_MILLING:
                return generateCreateSingleInputRecipe(menu, "milling");
            case CREATE_PRESSING:
                return generateCreateSingleInputRecipe(menu, "pressing");
            case CREATE_CUTTING:
                return generateCreateSingleInputRecipe(menu, "cutting");
            case CREATE_SANDPAPER_POLISHING:
                return generateCreateSingleInputRecipe(menu, "sandpaper_polishing");
            case CREATE_HAUNTING:
                return generateCreateSingleInputRecipe(menu, "haunting");
            case CREATE_SPLASHING:
                return generateCreateSingleInputRecipe(menu, "splashing");
            case CREATE_EMPTYING:
                return generateCreateSingleInputRecipe(menu, "emptying");
            case CREATE_WASHING:
                return generateCreateSingleInputRecipe(menu, "washing");
            case CREATE_DEPLOYING:
                return generateCreateDeployingRecipe(menu);
            case CREATE_MIXING:
                return generateCreateMultiInputRecipe(menu, "mixing");
            case CREATE_COMPACTING:
                return generateCreateMultiInputRecipe(menu, "compacting");
            default:
                return generateShapedRecipe(menu);
        }
    }

    public static String generateShapedRecipe(RecipeCreatorMenu menu) {
        ItemStack resultStack = menu.getResultStack();
        if (resultStack.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.result_empty");
        }

        List<ItemStack> ingredients = menu.getIngredientStacks();
        int minRow = 3;
        int maxRow = -1;
        int minColumn = 3;
        int maxColumn = -1;

        for (int index = 0; index < ingredients.size(); ++index) {
            if (ingredients.get(index).isEmpty()) {
                continue;
            }

            int row = index / 3;
            int column = index % 3;
            minRow = Math.min(minRow, row);
            maxRow = Math.max(maxRow, row);
            minColumn = Math.min(minColumn, column);
            maxColumn = Math.max(maxColumn, column);
        }

        if (maxRow < 0) {
            throw new IllegalStateException("message.visiblejs.error.ingredients_empty");
        }

        Map<String, Character> symbolByIngredient = new LinkedHashMap<>();
        Map<Character, String> ingredientEntries = new LinkedHashMap<>();
        List<String> patternRows = new ArrayList<>();
        char nextSymbol = 'A';

        for (int row = minRow; row <= maxRow; ++row) {
            StringBuilder rowPattern = new StringBuilder();
            for (int column = minColumn; column <= maxColumn; ++column) {
                ItemStack ingredientStack = ingredients.get(row * 3 + column);
                if (ingredientStack.isEmpty()) {
                    rowPattern.append(' ');
                    continue;
                }

                String ingredientKey = getIngredientKey(ingredientStack);
                Character symbol = symbolByIngredient.get(ingredientKey);
                if (symbol == null) {
                    if (nextSymbol > 'Z') {
                        throw new IllegalStateException("message.visiblejs.error.too_many_ingredients");
                    }
                    symbol = nextSymbol++;
                    symbolByIngredient.put(ingredientKey, symbol);
                    ingredientEntries.put(symbol, buildIngredientExpression(ingredientStack));
                }
                rowPattern.append(symbol);
            }
            patternRows.add(rowPattern.toString());
        }

        String resultExpression = buildResultExpression(resultStack);
        StringBuilder script = new StringBuilder();
        script.append("ServerEvents.recipes(event => {\n");
        script.append("  event.shaped(").append(resultExpression).append(", [\n");
        for (int index = 0; index < patternRows.size(); ++index) {
            script.append("    '").append(patternRows.get(index)).append("'");
            if (index < patternRows.size() - 1) {
                script.append(',');
            }
            script.append('\n');
        }
        script.append("  ], {\n");
        int entryIndex = 0;
        for (Map.Entry<Character, String> entry : ingredientEntries.entrySet()) {
            script.append("    ").append(entry.getKey()).append(": ").append(entry.getValue());
            if (entryIndex < ingredientEntries.size() - 1) {
                script.append(',');
            }
            script.append('\n');
            ++entryIndex;
        }
        script.append("  })\n");
        script.append("})");
        return script.toString();
    }

    public static String generateShapelessRecipe(RecipeCreatorMenu menu) {
        ItemStack resultStack = menu.getResultStack();
        if (resultStack.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.result_empty");
        }

        List<ItemStack> ingredients = menu.getShapelessIngredients();
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.ingredients_empty");
        }

        String resultExpression = buildResultExpression(resultStack);
        StringBuilder script = new StringBuilder();
        script.append("ServerEvents.recipes(event => {\n");
        script.append("  event.shapeless(").append(resultExpression).append(", [\n");

        for (int index = 0; index < ingredients.size(); ++index) {
            ItemStack stack = ingredients.get(index);
            script.append("    ").append(buildIngredientExpression(stack));
            if (index < ingredients.size() - 1) {
                script.append(',');
            }
            script.append('\n');
        }

        script.append("  ])\n");
        script.append("})");
        return script.toString();
    }

    public static String generateCookingRecipe(RecipeCreatorMenu menu, String method) {
        ItemStack resultStack = menu.getResultStack();
        if (resultStack.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.result_empty");
        }

        ItemStack ingredient = menu.getSingleIngredient();
        if (ingredient.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.center_empty");
        }

        String resultExpression = buildResultExpression(resultStack);
        String ingredientExpression = buildIngredientExpression(ingredient);

        StringBuilder script = new StringBuilder();
        script.append("ServerEvents.recipes(event => {\n");
        script.append("  event.").append(method).append("(").append(resultExpression)
              .append(", ").append(ingredientExpression).append(")\n");
        script.append("})");
        return script.toString();
    }

    public static String generateSmithingRecipe(RecipeCreatorMenu menu) {
        ItemStack resultStack = menu.getResultStack();
        if (resultStack.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.result_empty");
        }

        ItemStack template = menu.getSmithingTemplate();
        ItemStack base = menu.getSmithingBase();
        ItemStack addition = menu.getSmithingAddition();

        if (template.isEmpty() || base.isEmpty() || addition.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.smithing_slots");
        }

        String resultExpression = buildResultExpression(resultStack);
        String templateExpression = buildIngredientExpression(template);
        String baseExpression = buildIngredientExpression(base);
        String additionExpression = buildIngredientExpression(addition);

        StringBuilder script = new StringBuilder();
        script.append("ServerEvents.recipes(event => {\n");
        script.append("  event.smithing(").append(resultExpression).append(", ")
              .append(templateExpression).append(", ").append(baseExpression)
              .append(", ").append(additionExpression).append(")\n");
        script.append("})");
        return script.toString();
    }

    public static String generateStonecuttingRecipe(RecipeCreatorMenu menu) {
        ItemStack resultStack = menu.getResultStack();
        if (resultStack.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.result_empty");
        }

        ItemStack ingredient = menu.getSingleIngredient();
        if (ingredient.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.center_empty");
        }

        String resultExpression = buildResultExpression(resultStack);
        String ingredientExpression = buildIngredientExpression(ingredient);

        StringBuilder script = new StringBuilder();
        script.append("ServerEvents.recipes(event => {\n");
        script.append("  event.stonecutting(").append(resultExpression).append(", ")
              .append(ingredientExpression).append(")\n");
        script.append("})");
        return script.toString();
    }

    public static String generateCreateSingleInputRecipe(RecipeCreatorMenu menu, String method) {
        ItemStack resultStack = menu.getResultStack();
        if (resultStack.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.result_empty");
        }

        ItemStack ingredient = menu.getSingleIngredient();
        if (ingredient.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.center_empty");
        }

        return RecipeScriptPreview.createSingleInputRecipe(method, toItemId(resultStack), resultStack.getCount(), toItemId(ingredient));
    }

    public static String generateCreateDeployingRecipe(RecipeCreatorMenu menu) {
        ItemStack resultStack = menu.getResultStack();
        if (resultStack.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.result_empty");
        }

        ItemStack first = menu.getSmithingBase();
        ItemStack second = menu.getSmithingAddition();
        if (first.isEmpty() || second.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.center_empty");
        }

        return RecipeScriptPreview.createDualInputRecipe("deploying", toItemId(resultStack), resultStack.getCount(), toItemId(first), toItemId(second));
    }

    public static String generateCreateMultiInputRecipe(RecipeCreatorMenu menu, String method) {
        ItemStack resultStack = menu.getResultStack();
        if (resultStack.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.result_empty");
        }

        List<ItemStack> ingredients = menu.getShapelessIngredients();
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("message.visiblejs.error.ingredients_empty");
        }

        List<String> ingredientIds = new ArrayList<>(ingredients.size());
        for (ItemStack stack : ingredients) {
            ingredientIds.add(toItemId(stack));
        }
        return RecipeScriptPreview.createMultiInputRecipe(method, toItemId(resultStack), resultStack.getCount(), ingredientIds);
    }

    private static String buildResultExpression(ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) {
            throw new IllegalStateException("message.visiblejs.error.parse_result");
        }
        if (stack.getCount() > 1) {
            return "Item.of('" + itemId + "', " + stack.getCount() + ")";
        }
        return "'" + itemId + "'";
    }

    private static String buildIngredientExpression(ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) {
            throw new IllegalStateException("message.visiblejs.error.parse_ingredient");
        }
        return "'" + itemId + "'";
    }

    private static String getIngredientKey(ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) {
            throw new IllegalStateException("message.visiblejs.error.parse_ingredient");
        }
        return itemId.toString();
    }

    private static String toItemId(ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) {
            throw new IllegalStateException("message.visiblejs.error.parse_ingredient");
        }
        return itemId.toString();
    }
}
