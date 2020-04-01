package me.oriharel.customrecipes.api;

import me.oriharel.customrecipes.CustomRecipes;
import me.oriharel.customrecipes.RecipesManager;

public class CustomRecipesAPI {

    private static CustomRecipes implementation;

    private static RecipesManager recipesManager;

    /**
     * @return The CustomRecipes implementation
     */
    public static CustomRecipes getImplementation() {
        return implementation;
    }

    /**
     * @param implementation the implementation to set
     */
    public static void setImplementation(CustomRecipes implementation) {
        if (CustomRecipesAPI.implementation != null) {
            throw new IllegalArgumentException("Cannot set API implementation twice");
        }

        CustomRecipesAPI.implementation = implementation;
    }

    /**
     * @return The RecipesManager implementation
     */
    public static RecipesManager getRecipesManager() {
        if (recipesManager == null) {
            recipesManager = implementation.getRecipesManager();
        }

        return recipesManager;
    }

}

