package me.oriharel.customrecipes.recipe.ingredient;

import me.oriharel.customrecipes.recipe.item.RecipeItem;

import java.io.Serializable;

public interface IIngredient extends Serializable {
    String getIngredientKey();

    RecipeItem getIngredient();
}
