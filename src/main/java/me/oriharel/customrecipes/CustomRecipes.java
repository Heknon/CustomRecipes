package me.oriharel.customrecipes;

import me.oriharel.customrecipes.api.CustomRecipesAPI;
import me.oriharel.customrecipes.config.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomRecipes extends JavaPlugin {

    private static CustomRecipes INSTANCE;
    private FileManager fileManager;
    private RecipesManager recipesManager;

    public static CustomRecipes getInstance() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage("Starting up CustomRecipes");

        fileManager = new FileManager(this);
        fileManager.getConfig("config.yml").copyDefaults(false).save();
        fileManager.getConfig("recipes.yml").copyDefaults(false).save();
        recipesManager = new RecipesManager(this);

        CustomRecipesAPI.setImplementation(this);
    }

    @Override
    public void onDisable() {

    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public RecipesManager getRecipesManager() {
        return recipesManager;
    }
}
