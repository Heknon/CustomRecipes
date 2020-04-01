package me.oriharel.customrecipes.recipe.ingredient;

import me.oriharel.customrecipes.recipe.item.IRecipeItem;

public interface IIngredient {
    String getIngredientKey();

    IRecipeItem getIngredient();
}
