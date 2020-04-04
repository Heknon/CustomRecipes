package me.oriharel.customrecipes.recipe.ingredient;

import me.oriharel.customrecipes.recipe.item.IRecipeItem;

import java.io.Serializable;

public interface IIngredient extends Serializable {
    String getIngredientKey();

    IRecipeItem getIngredient();
}
