package me.oriharel.customrecipes.recipe.item;

import com.google.gson.Gson;
import me.oriharel.customrecipes.CustomRecipes;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.error.YAMLException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeItem extends ItemStack implements IRecipeItem {
    private final String key;
    private transient final ConfigurationSection section;
    private transient NBTTagCompound nbtTagCompound;
    private String displayName;
    private int recipeItemAmount = -1;
    private Material material;
    private List<String> lore;
    private Map<Enchantment, Integer> _enchantments;
    private ItemStack itemStackWithNBT = null;

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
        this.nbtTagCompound = new NBTTagCompound();
        buildItemStack();
    }

    @Override
    public NBTTagCompound getNBTTagCompound() {
        if (nbtTagCompound == null && this.section.isSet("nbt")) {
            try {
                Constructor<NBTTagCompound> nbtTagCompoundConstructor = NBTTagCompound.class.getDeclaredConstructor(Map.class);
                nbtTagCompoundConstructor.setAccessible(true);
                String nbt = this.section.getString("nbt");
                if (nbt == null || nbt.equalsIgnoreCase("")) {
                    nbtTagCompound = new NBTTagCompound();
                    return nbtTagCompound;
                }
                this.nbtTagCompound = nbtFromMap(new Gson().fromJson(new String(Base64.decodeBase64(nbt)), Map.class));

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
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
        if (this.recipeItemAmount == -1) this.recipeItemAmount = this.section.getInt("amount", 1);
        return this.recipeItemAmount;
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
        try {
            this.setType(getMaterial());
            this.setAmount(getAmount());
            this.setItemMeta(new ItemStack(getMaterial(), getAmount()).getItemMeta());
            Field metaField = ItemStack.class.getDeclaredField("meta");
            metaField.setAccessible(true);
            ItemMeta meta = (ItemMeta) metaField.get(this);
            Class craftMetaItem = Class.forName("org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaItem");
            Field unhandledTagsField = craftMetaItem.getDeclaredField("unhandledTags");
            unhandledTagsField.setAccessible(true);
            Field nbtMapField = NBTTagCompound.class.getDeclaredField("map");
            nbtMapField.setAccessible(true);
            NBTTagCompound tagCompoundToSet = getNBTTagCompound();
            if (tagCompoundToSet != null) {
                Map<String, NBTBase> tagCompoundMapToSet = (Map<String, NBTBase>) nbtMapField.get(tagCompoundToSet);
                unhandledTagsField.set(meta, tagCompoundMapToSet);
            }
            if (getDisplayName() != null) meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            if (getLore() != null)
                meta.setLore(lore.stream().map(lorePart -> ChatColor.translateAlternateColorCodes('&', lorePart)).collect(Collectors.toList()));
            if (_getEnchantments() != null)
                this.addEnchantments(_enchantments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private NBTTagCompound nbtFromMap(Map<String, Object> map) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            nbtTagCompound.set(entry.getKey(), nbtFromMapHelper(entry.getValue()));
        }
        return nbtTagCompound;
    }

    private NBTBase nbtFromMapHelper(Object value) {
        if (value instanceof Map) {
            return nbtFromMap((Map<String, Object>) value);
        } else if (value instanceof List) {
            NBTTagList nbtTagList = new NBTTagList();
            List list = ((List) value);
            for (Object o : list) {
                nbtTagList.add(nbtFromMapHelper(o));
            }
            return nbtTagList;
        } else if (value instanceof String) {
            return NBTTagString.a(((String) value).replace("Â§", "§"));
        } else if (value instanceof Integer) {
            return NBTTagInt.a((Integer) value);
        } else if (value instanceof Byte) {
            return NBTTagByte.a((byte) value);
        } else if (value instanceof byte[]) {
            return new NBTTagByteArray((byte[]) value);
        } else if (value instanceof Boolean) {
            return NBTTagByte.a((Boolean) value);
        } else if (value instanceof Double) {
            return NBTTagDouble.a((Double) value);
        } else if (value instanceof Float) {
            return NBTTagFloat.a((Float) value);
        } else if (value instanceof int[]) {
            return new NBTTagIntArray((int[]) value);
        } else if (value instanceof Long) {
            return NBTTagLong.a((Long) value);
        } else if (value instanceof Short) {
            return NBTTagShort.a((Short) value);
        }
        return null;
    }

    public String getKey() {
        return key;
    }
}
