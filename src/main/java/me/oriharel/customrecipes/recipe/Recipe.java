package me.oriharel.customrecipes.recipe;

import me.oriharel.customrecipes.CustomRecipes;
import me.oriharel.customrecipes.recipe.ingredient.IIngredient;
import me.oriharel.customrecipes.recipe.ingredient.Ingredient;
import me.oriharel.customrecipes.recipe.ingredient.ReferencedResultIngredient;
import me.oriharel.customrecipes.recipe.item.RecipeResultReference;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Recipe implements Serializable {

    private RecipeResultReference result;
    private List<IIngredient> ingredients;
    private transient CustomRecipes customRecipes;
    private transient org.bukkit.inventory.Recipe recipe;
    private String recipeKey;
    private List<String> recipeShape;
    private Set<String> ingredientKeys;
    private boolean byReference;
    private boolean shapeless;

    public Recipe(CustomRecipes customRecipes, String recipeKey, List<String> recipeShape, Set<String> ingredientKeys, boolean shapeless, boolean byReference) {
        this.customRecipes = customRecipes;
        this.shapeless = shapeless;
        this.recipeKey = recipeKey;
        this.recipeShape = recipeShape;
        this.ingredientKeys = ingredientKeys;
        this.byReference = byReference;
        this.result = new RecipeResultReference(recipeKey, customRecipes);
        this.ingredients = ingredientKeys.stream().map(ingredientKey -> byReference ? new ReferencedResultIngredient(getReferencedRecipeKey(recipeKey, ingredientKey),
                ingredientKey, customRecipes) :
                new Ingredient(recipeKey, ingredientKey, customRecipes)).collect(Collectors.toList()
        );
        NamespacedKey key = new NamespacedKey(customRecipes, recipeKey);

        if (shapeless) {
            if (ingredients.size() != 1)
                throw new YAMLException("Invalid configuration! Shapeless recipe \"" + recipeKey + "\" must have exactly 1 ingredient!");
            ShapelessRecipe recipe = new ShapelessRecipe(key, this.result.getItemStackWithNBT());
            recipe.addIngredient(new RecipeChoice.ExactChoice(ingredients.get(0).getIngredient().getItemStackWithNBT()));
            this.recipe = recipe;
        } else {
            ShapedRecipe recipe = new ShapedRecipe(key, this.result.getItemStackWithNBT());
            switch (recipeShape.size()) {
                case 1:
                    recipe.shape(recipeShape.get(0));
                    break;
                case 2:
                    recipe.shape(recipeShape.get(0), recipeShape.get(1));
                    break;
                case 3:
                    recipe.shape(recipeShape.get(0), recipeShape.get(1), recipeShape.get(2));
                    break;
                default:
                    throw new YAMLException("Invalid recipe shape in recipe: \"" + recipeKey + "\". Must be of length 1, 2 or 3");
            }
            for (IIngredient ingredient : this.ingredients) {
                recipe.setIngredient(ingredient.getIngredientKey().charAt(0), new RecipeChoice.ExactChoice(ingredient.getIngredient().getItemStackWithNBT()));
            }
            this.recipe = recipe;
        }
    }

    public String getRecipeKey() {
        return recipeKey;
    }

    public List<String> getRecipeShape() {
        return recipeShape;
    }

    public Set<String> getIngredientKeys() {
        return ingredientKeys;
    }

    public boolean isByReference() {
        return byReference;
    }

    public RecipeResultReference getResult() {
        return result;
    }

    public List<IIngredient> getIngredients() {
        return ingredients;
    }

    public org.bukkit.inventory.Recipe getRecipe() {
        return recipe;
    }

    private String getReferencedRecipeKey(String recipeKey, String ingredientKey) {
        ConfigurationSection recipesSection = customRecipes.getFileManager().getConfig("recipes.yml").get().getConfigurationSection("recipes");
        return recipesSection.getString(recipeKey + ".ingredients." + ingredientKey + ".ref");
    }

    public boolean isShapeless() {
        return shapeless;
    }
}
