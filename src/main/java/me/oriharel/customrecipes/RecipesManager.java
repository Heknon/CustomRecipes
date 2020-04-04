package me.oriharel.customrecipes;

import com.sun.istack.internal.NotNull;
import me.oriharel.customrecipes.config.FileManager;
import me.oriharel.customrecipes.recipe.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class RecipesManager {
    private final CustomRecipes customRecipes;
    private List<Recipe> recipes;
    private List<Runnable> recipesLoadedCallbacks;

    public RecipesManager(CustomRecipes customRecipes) {
        this.customRecipes = customRecipes;
        this.recipesLoadedCallbacks = new ArrayList<>();
        FileManager.Config config = customRecipes.getFileManager().getConfig("recipes.yml");
        YamlConfiguration configLoad = config.get();

        this.recipes = new ArrayList<Recipe>();
        if (!configLoad.isConfigurationSection("recipes")) {
            configLoad.createSection("recipes");
            config.save();
            return;
        }

        ConfigurationSection recipesSection = configLoad.getConfigurationSection("recipes");
        Set<String> recipeKeys = recipesSection.getKeys(false);
        Bukkit.getScheduler().runTaskAsynchronously(customRecipes, () -> {
            for (String key : recipeKeys) {
                Bukkit.getServer().getLogger().log(Level.INFO, "Starting build recipe process for \"" + key + "\" recipe.");
                Recipe recipe = buildRecipe(key, recipesSection);
                Bukkit.getScheduler().runTask(customRecipes, () -> {
                    boolean success = addRecipe(recipe);
                    if (success)
                        Bukkit.getServer().getLogger().log(Level.INFO, "The recipe \"" + key + "\" was successfully registered!");
                    else Bukkit.getServer().getLogger().log(Level.INFO, "Failed to register recipe \"" + key + "\"");
                });
            }
            this.recipesLoadedCallbacks.forEach(Runnable::run);
        });
    }

    public void registerRecipesDoneCallback(Runnable callback) {
        this.recipesLoadedCallbacks.add(callback);
    }

    /**
     * Add a recipe to the recipe registry
     *
     * @param recipe the Recipe object
     * @return true if success false otherwise
     */
    public boolean addRecipe(Recipe recipe) {
        boolean success = Bukkit.addRecipe(recipe.getRecipe());
        if (success) this.recipes.add(recipe);
        return success;
    }

    private Recipe buildRecipe(@NotNull String key, @NotNull ConfigurationSection recipesSection) {
        ConfigurationSection recipeSection = recipesSection.getConfigurationSection(key);
        return new Recipe(
                customRecipes,
                key,
                recipeSection.getStringList("recipe_shape"),
                recipeSection.getConfigurationSection("ingredients").getKeys(false),
                recipeSection.getBoolean("shapeless_recipe"),
                recipeSection.getBoolean("by_reference")
        );
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }
}
