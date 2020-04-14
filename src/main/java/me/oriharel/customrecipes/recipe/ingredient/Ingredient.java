package me.oriharel.customrecipes.recipe.ingredient;

import me.oriharel.customrecipes.CustomRecipes;
import me.oriharel.customrecipes.recipe.item.IRecipeItem;
import me.oriharel.customrecipes.recipe.item.RecipeIngredient;
import me.oriharel.customrecipes.recipe.item.RecipeItem;

public class Ingredient implements IIngredient {

    private final String ingredientKey;
    private final RecipeItem ingredient;

    public Ingredient(String key, String ingredientKey, CustomRecipes customRecipes) {
        this.ingredientKey = ingredientKey;
        this.ingredient = new RecipeIngredient(key, ingredientKey, customRecipes);
    }

    @Override
    public String getIngredientKey() {
        return ingredientKey;
    }

    @Override
    public RecipeItem getIngredient() {
        return ingredient;
    }
}
