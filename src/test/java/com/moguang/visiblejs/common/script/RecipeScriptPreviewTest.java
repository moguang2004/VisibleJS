package com.moguang.visiblejs.common.script;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecipeScriptPreviewTest {
    @Test
    void createSingleInputRecipeUsesCreateSyntax() {
        String script = RecipeScriptPreview.createSingleInputRecipe(
                "crushing",
                "minecraft:iron_ingot",
                2,
                "minecraft:raw_iron"
        );

        assertEquals("""
                ServerEvents.recipes(event => {
                  event.recipes.create.crushing(Item.of('minecraft:iron_ingot', 2), 'minecraft:raw_iron')
                })""".trim(), script);
    }

    @Test
    void createMultiInputRecipeWrapsIngredientList() {
        String script = RecipeScriptPreview.createMultiInputRecipe(
                "mixing",
                "minecraft:glass",
                1,
                List.of("minecraft:sand", "minecraft:coal")
        );

        assertEquals("""
                ServerEvents.recipes(event => {
                  event.recipes.create.mixing('minecraft:glass', ['minecraft:sand', 'minecraft:coal'])
                })""".trim(), script);
    }

    @Test
    void createDualInputRecipeUsesTwoIngredients() {
        String script = RecipeScriptPreview.createDualInputRecipe(
                "deploying",
                "minecraft:diamond_sword",
                1,
                "minecraft:iron_sword",
                "minecraft:diamond"
        );

        assertEquals("""
                ServerEvents.recipes(event => {
                  event.recipes.create.deploying('minecraft:diamond_sword', 'minecraft:iron_sword', 'minecraft:diamond')
                })""".trim(), script);
    }

    @Test
    void createSingleInputRecipeSupportsSplashing() {
        String script = RecipeScriptPreview.createSingleInputRecipe(
                "splashing",
                "minecraft:gravel",
                1,
                "minecraft:iron_nugget"
        );

        assertEquals("""
                ServerEvents.recipes(event => {
                  event.recipes.create.splashing('minecraft:gravel', 'minecraft:iron_nugget')
                })""".trim(), script);
    }

    @Test
    void createSingleInputRecipeSupportsEmptying() {
        String script = RecipeScriptPreview.createSingleInputRecipe(
                "emptying",
                "minecraft:bucket",
                1,
                "minecraft:water_bucket"
        );

        assertEquals("""
                ServerEvents.recipes(event => {
                  event.recipes.create.emptying('minecraft:bucket', 'minecraft:water_bucket')
                })""".trim(), script);
    }

    @Test
    void createSingleInputRecipeSupportsWashing() {
        String script = RecipeScriptPreview.createSingleInputRecipe(
                "washing",
                "minecraft:string",
                1,
                "minecraft:stick"
        );

        assertEquals("""
                ServerEvents.recipes(event => {
                  event.recipes.create.washing('minecraft:string', 'minecraft:stick')
                })""".trim(), script);
    }
}

