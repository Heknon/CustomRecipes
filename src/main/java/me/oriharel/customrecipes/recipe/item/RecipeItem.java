package me.oriharel.customrecipes.recipe.item;

import me.oriharel.customrecipes.CustomRecipes;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class RecipeItem extends ItemStack implements IRecipeItem, Serializable {
    private final String key;
    private transient final ConfigurationSection section;
    private me.oriharel.customrecipes.serialize.NBTTagCompound nbtTagCompound;
    private String displayName;
    private int recipeItemAmount = -1;
    private Material material;
    private List<String> lore;
    private Map<Enchantment, Integer> _enchantments;

    public RecipeItem(String key, CustomRecipes customRecipes) {
        super();
        this.key = key;
        if (this instanceof RecipeResultReference)
            this.section = customRecipes.getFileManager().getConfig("recipes.yml").get().getConfigurationSection("recipes." + key + ".item");
        else this.section = null;
        handleNullSection();
        buildItemStack();

    }

    public RecipeItem(String key, String ingredientKey, CustomRecipes customRecipes) {
        super();
        this.key = key;
        if (this instanceof RecipeIngredient)
            this.section = customRecipes.getFileManager().getConfig("recipes.yml").get().getConfigurationSection("recipes." + key + ".ingredients." + ingredientKey);
        else this.section = null;
        handleNullSection();
        this.nbtTagCompound = new me.oriharel.customrecipes.serialize.NBTTagCompound();
        buildItemStack();
    }

    @Override
    public me.oriharel.customrecipes.serialize.NBTTagCompound getNBTTagCompound() {
        if (nbtTagCompound == null && this.section.isConfigurationSection("nbt")) {
            nbtTagCompound = new me.oriharel.customrecipes.serialize.NBTTagCompound();
            this.section.getConfigurationSection("nbt").getValues(false).entrySet().forEach(e -> nbtTagCompound.setString(e.getKey(), (String) e.getValue()));
        }
        return nbtTagCompound;
    }

    @Override
    public String getDisplayName() {
        if (displayName == null) this.displayName = this.section.getString("display_name");
        return displayName;
    }

    @Override
    public Material getMaterial() {
        if (material == null) {
            this.material = Material.getMaterial(this.section.getString("material", "_"));
            if (this.material == null)
                throw new YAMLException("Invalid material name in section: \"" + this.section.getCurrentPath() + "\". View https://hub.spigotmc" +
                        ".org/javadocs/spigot/org/bukkit/Material.html for a list of valid materials.");
        }
        return material;
    }

    @Override
    public List<String> getLore() {
        if (lore == null) this.lore = this.section.getStringList("lore");
        return lore;
    }

    @Override
    public int getAmount() {
        if (RecipeItem.this.recipeItemAmount == -1) RecipeItem.this.recipeItemAmount = this.section.getInt("amount", 1);
        return RecipeItem.this.recipeItemAmount;
    }

    @Override
    public Map<Enchantment, Integer> _getEnchantments() {
        if (this._enchantments == null && this.section.isConfigurationSection("enchantments"))
            this._enchantments = this.section.getConfigurationSection("enchantments").getValues(false)
                    .entrySet()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    k -> Enchantment.getByKey(new NamespacedKey(NamespacedKey.MINECRAFT, k.getKey())),
                                    v -> Integer.parseInt((String) v.getValue()))
                    );
        return this._enchantments;
    }

    private void handleNullSection() {
        if (this.section == null) {
            throw new YAMLException("Failed to construct RecipeItem. Failed to get instance of configuration section for the item.");
        }
    }

    private void buildItemStack() {
        ItemMeta meta = getItemMeta();
        if (meta == null) meta = new ItemStack(getMaterial()).getItemMeta();
        if (getDisplayName() != null) meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if (getLore() != null)
            meta.setLore(lore.stream().map(lorePart -> ChatColor.translateAlternateColorCodes('&', lorePart)).collect(Collectors.toList()));
        if (_getEnchantments() != null)
            addEnchantments(_enchantments);
        setType(getMaterial());
        setAmount(RecipeItem.this.recipeItemAmount);
        setItemMeta(meta);
    }

    @Override
    public ItemStack getItemStackWithNBT() {
        net.minecraft.server.v1_15_R1.ItemStack is = CraftItemStack.asNMSCopy(this);
        NBTTagCompound tagCompound = is.getTag();
        if (getNBTTagCompound() != null) for (String key : nbtTagCompound.getKeys()) {
            tagCompound.setString(key, nbtTagCompound.getString(key));
        }
        is.setTag(tagCompound);
        return CraftItemStack.asBukkitCopy(is);
    }

    public String getKey() {
        return key;
    }
}
