package me.oriharel.customrecipes.recipe.item;

import me.oriharel.customrecipes.CustomRecipes;

import java.io.Serializable;

public class RecipeResultReference extends RecipeItem implements Serializable {
    public RecipeResultReference(String key, CustomRecipes customRecipes) {
        super(key, customRecipes);
    }
}
