package me.oriharel.customrecipes.recipe.ingredient;

import me.oriharel.customrecipes.CustomRecipes;
import me.oriharel.customrecipes.recipe.item.IRecipeItem;
import me.oriharel.customrecipes.recipe.item.RecipeIngredient;

public class Ingredient implements IIngredient {

    private final String ingredientKey;
    private final IRecipeItem ingredient;

    public Ingredient(String key, String ingredientKey, CustomRecipes customRecipes) {
        this.ingredientKey = ingredientKey;
        this.ingredient = new RecipeIngredient(key, ingredientKey, customRecipes);
    }

    @Override
    public String getIngredientKey() {
        return ingredientKey;
    }

    @Override
    public IRecipeItem getIngredient() {
        return ingredient;
    }
}
