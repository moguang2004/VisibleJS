package com.moguang.visiblejs.common.script;

import java.util.List;
import java.util.StringJoiner;

public final class RecipeScriptPreview {
    private RecipeScriptPreview() {
    }

    public static String createSingleInputRecipe(String method, String resultId, int resultCount, String ingredientId) {
        return wrap(method, buildResultExpression(resultId, resultCount), buildIngredientExpression(ingredientId));
    }

    public static String createDualInputRecipe(String method, String resultId, int resultCount, String firstIngredientId, String secondIngredientId) {
        return wrap(method, buildResultExpression(resultId, resultCount), buildIngredientExpression(firstIngredientId), buildIngredientExpression(secondIngredientId));
    }

    public static String createMultiInputRecipe(String method, String resultId, int resultCount, List<String> ingredientIds) {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (String ingredientId : ingredientIds) {
            joiner.add(buildIngredientExpression(ingredientId));
        }
        return wrap(method, buildResultExpression(resultId, resultCount), joiner.toString());
    }

    private static String wrap(String method, String resultExpression, String... inputExpressions) {
        StringBuilder script = new StringBuilder();
        script.append("ServerEvents.recipes(event => {\n");
        script.append("  event.recipes.create.").append(method).append("(").append(resultExpression);
        for (String inputExpression : inputExpressions) {
            script.append(", ").append(inputExpression);
        }
        script.append(")\n");
        script.append("})");
        return script.toString();
    }

    private static String buildResultExpression(String itemId, int count) {
        if (count > 1) {
            return "Item.of('" + itemId + "', " + count + ")";
        }
        return "'" + itemId + "'";
    }

    private static String buildIngredientExpression(String itemId) {
        return "'" + itemId + "'";
    }
}


