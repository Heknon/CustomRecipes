package me.oriharel.customrecipes;

import com.google.gson.GsonBuilder;
import me.oriharel.customrecipes.api.CustomRecipesAPI;
import me.oriharel.customrecipes.config.FileManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.NBTBase;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

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
        File folder = getDataFolder();
        File recipes = new File(folder, "recipes.yml");
        if (!folder.exists()) folder.mkdir();
        if (!recipes.exists()) {
            fileManager.getConfig("recipes.yml").copyDefaults(true).save();
        }
        fileManager.getConfig("recipes.yml").copyDefaults(false).save();
        recipesManager = new RecipesManager(this);
        getCommand("customrecipes").setExecutor((commandSender, cmd, s, strings) -> {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("You must be a player to run this command.");
                return true;
            }
            if (strings.length == 1 && strings[0].equalsIgnoreCase("shownbt") && commandSender.hasPermission("customrecipes.shownbt")) {
                ItemStack item = ((Player) commandSender).getInventory().getItemInMainHand();
                if (item.getType().equals(Material.AIR)) {
                    commandSender.sendMessage("You must hold an item.");
                    return true;
                }
                NBTTagCompound tagCompound = CraftItemStack.asNMSCopy(item).getTag();
                if (tagCompound == null) {
                    commandSender.sendMessage("The item you are holding doesn't have NBT.");
                    return true;
                }
                commandSender.sendMessage("copy this into the item section.");
                try {
                    String nbtData = Base64.encodeBase64String(tagCompound.toString().getBytes());
                    TextComponent a = new TextComponent("Click me to copy!");
                    a.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, nbtData));
                    a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(nbtData).create()));
                    commandSender.spigot().sendMessage(a);
                } catch (Exception ignored) {

                }

            }
            return true;
        });

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
