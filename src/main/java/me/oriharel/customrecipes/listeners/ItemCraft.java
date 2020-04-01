package me.oriharel.customrecipes.listeners;

import me.oriharel.customrecipes.CustomRecipes;
import me.oriharel.customrecipes.recipe.Recipe;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemCraft implements Listener {
    private CustomRecipes customRecipes;

    public ItemCraft(CustomRecipes customRecipes) {
        this.customRecipes = customRecipes;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        List<Recipe> recipes = customRecipes.getRecipesManager().getShapelessRecipes();
        for (ItemStack is : e.getInventory().getMatrix()) {
            for (Recipe recipe : recipes) {
                System.out.println(recipe.getIngredients());
                System.out.println(recipe.getIngredients().get(0));
                if (is.equals(recipe.getIngredients().get(0).getIngredient().getItemStackWithNBT())) {
                    e.getInventory().setResult(recipe.getResult());
                    break;
                }
            }
        }
    }

}
