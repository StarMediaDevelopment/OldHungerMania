package net.hungermania.hungergames.loot;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.manialib.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter @Setter
public class Loot {
    private int id, maxAmount;
    private Material material;
    private String name;
    private int weight;
    
    public Loot(Material material, String name, int weight) {
        this.material = material;
        this.name = name;
        this.weight = weight;
    }
    
    public Loot(Material material, int weight) {
        this.material = material;
        this.name = Utils.capitalizeEveryWord(material.name());
        this.weight = weight;
    }
    
    public Loot(Material material, String name, int weight, int maxAmount) {
        this.material = material;
        this.name = name;
        this.weight = weight;
        this.maxAmount = maxAmount;
    }
    
    public Loot(Material material, int weight, int maxAmount) {
        this.material = material;
        this.name = Utils.capitalizeEveryWord(material.name());
        this.weight = weight;
        this.maxAmount = maxAmount;
    }
    
    public Loot(int id, Material material, String name, int weight, int maxAmount) {
        this.id = id;
        this.material = material;
        this.name = name;
        this.weight = weight;
        this.maxAmount = maxAmount;
    }
    
    public ItemStack generateItemStack() {
        int amount;
        if (this.maxAmount != 0) {
            amount = ManiaCore.RANDOM.nextInt(maxAmount - 1) + 1;
        } else {
            amount = 1;
        }
        ItemStack itemStack = new ItemStack(this.material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!name.equalsIgnoreCase(material.name().replace("_", " "))) {
            itemMeta.setDisplayName(ManiaUtils.color("&f" + this.name));
        }
        if (this.material == Material.FLINT_AND_STEEL) {
            itemStack.setDurability((short) (material.getMaxDurability() -4));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
