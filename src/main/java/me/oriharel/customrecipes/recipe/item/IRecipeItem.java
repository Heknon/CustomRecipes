package me.oriharel.customrecipes.recipe.item;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface IRecipeItem {
    NBTTagCompound getNBTTagCompound();

    String getDisplayName();

    int getAmount();

    Material getMaterial();

    List<String> getLore();

    Map<Enchantment, Integer> _getEnchantments();

    ItemStack getItemStackWithNBT();
}
