package me.oriharel.customrecipes;

import com.sun.istack.internal.NotNull;
import me.oriharel.customrecipes.config.FileManager;
import me.oriharel.customrecipes.recipe.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class RecipesManager {
    private final CustomRecipes customRecipes;
    private List<Recipe> recipes;

    public RecipesManager(CustomRecipes customRecipes) {
        this.customRecipes = customRecipes;
        FileManager.Config config = customRecipes.getFileManager().getConfig(new File(customRecipes.getDataFolder(), "recipes.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        this.recipes = new ArrayList<Recipe>();
        if (!configLoad.isConfigurationSection("recipes")) {
            configLoad.createSection("recipes");
            try {
                configLoad.save(config.getFile());
            } catch (IOException e) {
                Bukkit.getServer().getLogger().log(Level.WARNING, "CustomRecipes | Error: Unable to create configuration section `recipes`.");
            }
            return;
        }

        ConfigurationSection recipesSection = configLoad.getConfigurationSection("recipes");
        for (String key : recipesSection.getKeys(false)) {
            Bukkit.getServer().getLogger().log(Level.INFO, "Starting build recipe process for \"" + key + "\" recipe.");
            final Recipe[] recipe = new Recipe[1];
            Bukkit.getScheduler().runTaskAsynchronously(customRecipes, () -> {
                recipe[0] = buildRecipe(key, recipesSection);
                Bukkit.getScheduler().runTask(customRecipes, () -> {
                    boolean success = addRecipe(recipe[0]);
                    if (success)
                        Bukkit.getServer().getLogger().log(Level.INFO, "The recipe \"" + key + "\" was successfully registered!");
                    else Bukkit.getServer().getLogger().log(Level.INFO, "Failed to register recipe \"" + key + "\"");
                });
            });
        }
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
