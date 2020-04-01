package me.oriharel.customrecipes.recipe.ingredient;

import me.oriharel.customrecipes.CustomRecipes;
import me.oriharel.customrecipes.recipe.item.IRecipeItem;
import me.oriharel.customrecipes.recipe.item.RecipeResultReference;

public class ReferencedResultIngredient implements IIngredient {

    private final String ingredientKey;
    private final IRecipeItem ingredient;

    public ReferencedResultIngredient(String key, String ingredientKey, CustomRecipes customRecipes) {
        this.ingredientKey = ingredientKey;
        this.ingredient = new RecipeResultReference(key, customRecipes);
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
