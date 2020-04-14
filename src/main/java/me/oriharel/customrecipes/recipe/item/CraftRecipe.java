package me.oriharel.customrecipes.recipe.item;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

public class CraftRecipe extends ShapedRecipes {

    private int width;
    private int height;
    private NonNullList<RecipeItemStack> items;
    private ShapedRecipe shapedRecipe;

    public CraftRecipe(MinecraftKey minecraftkey, String s, int i, int j, NonNullList<RecipeItemStack> nonnulllist, ItemStack itemstack) {
        super(minecraftkey, s, i, j, nonnulllist, itemstack);
    }

    public CraftRecipe(ShapedRecipe shapedRecipe) {
        super(CraftNamespacedKey.toMinecraft(shapedRecipe.getKey()), shapedRecipe.getGroup(), shapedRecipe.getShape()[0].length(), shapedRecipe.getShape().length,
                getRecipeItemStackList(shapedRecipe), CraftItemStack.asNMSCopy(shapedRecipe.getResult()));
        this.shapedRecipe = shapedRecipe;
        try {
            Field widthField = ShapedRecipes.class.getDeclaredField("width");
            Field heightField = ShapedRecipes.class.getDeclaredField("height");
            Field itemsField = ShapedRecipes.class.getDeclaredField("items");
            widthField.setAccessible(true);
            heightField.setAccessible(true);
            itemsField.setAccessible(true);
            this.width = (int) widthField.get(this);
            this.height = (int) heightField.get(this);
            this.items = (NonNullList<RecipeItemStack>) itemsField.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean a(InventoryCrafting inventorycrafting, World world) {
        for(int i = 0; i <= inventorycrafting.g() - this.width; ++i) {
            for(int j = 0; j <= inventorycrafting.f() - this.height; ++j) {
                if (this.validatePosition(inventorycrafting, i, j, true)) {
                    return true;
                }

                if (this.validatePosition(inventorycrafting, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validatePosition(InventoryCrafting inventorycrafting, int i, int j, boolean flag) {
        for(int k = 0; k < inventorycrafting.g(); ++k) {
            for(int l = 0; l < inventorycrafting.f(); ++l) {
                int i1 = k - i;
                int j1 = l - j;
                RecipeItemStack recipeitemstack = RecipeItemStack.a;
                if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
                    if (flag) {
                        recipeitemstack = this.items.get(this.width - i1 - 1 + j1 * this.width);
                    } else {
                        recipeitemstack = this.items.get(i1 + j1 * this.width);
                    }
                }

                ItemStack compareTo = inventorycrafting.getItem(k + l * inventorycrafting.g());
                if (!test(recipeitemstack, compareTo)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean test(@Nullable RecipeItemStack itemStack, ItemStack itemStack2) {
        int lengthOfCFieldValue = 0;
        try {
            Field cField = RecipeItemStack.class.getDeclaredField("c");
            cField.setAccessible(true);
            lengthOfCFieldValue = ((RecipeItemStack.Provider[]) cField.get(itemStack)).length;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (itemStack2 == null) {
            return false;
        } else if (lengthOfCFieldValue == 0) {
            return itemStack2.isEmpty();
        } else {
            itemStack.buildChoices();
            ItemStack[] aitemstack = itemStack.choices;
            int i = aitemstack.length;

            for (ItemStack itemstack1 : aitemstack) {
                if (itemStack.exact) {
                    if (itemstack1.getItem() == itemStack2.getItem() && ItemStack.equals(itemStack2, itemstack1) && itemstack1.getCount() == itemStack2.getCount()) {
                        return true;
                    }
                } else if (itemstack1.getItem() == itemStack2.getItem() && itemstack1.getCount() == itemStack2.getCount()) {
                    return true;
                }
            }

            return false;
        }
    }

    public void addToCraftingManager() {
        MinecraftServer.getServer().getCraftingManager().addRecipe(this);
    }

    private static NonNullList<RecipeItemStack> getRecipeItemStackList(ShapedRecipe shapedRecipe) {
        String[] shape = shapedRecipe.getShape();
        Map<Character, RecipeChoice> ingred = shapedRecipe.getChoiceMap();
        int width = shape[0].length();
        NonNullList<RecipeItemStack> data = NonNullList.a(shape.length * width, RecipeItemStack.a);

        for (int i = 0; i < shape.length; ++i) {
            String row = shape[i];

            for (int j = 0; j < row.length(); ++j) {
                data.set(i * width + j, CraftShapedRecipe.fromBukkitRecipe(shapedRecipe).toNMS(ingred.get(row.charAt(j)), false));
            }
        }
        return data;
    }
}
