package me.oriharel.customrecipes.recipe.item;

import me.oriharel.customrecipes.CustomRecipes;

import java.io.Serializable;

public class RecipeIngredient extends RecipeItem implements Serializable {
    public RecipeIngredient(String key, String ingredientKey, CustomRecipes customRecipes) {
        super(key, ingredientKey, customRecipes);
    }
}
