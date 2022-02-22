package net.hungermania.hungergames.game.sponsoring;

import net.hungermania.manialib.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class SponsorItem {
    protected String name;
    protected Material material;
    protected int amount;
    protected SponsorType type;

    public SponsorItem(String name, Material material, int amount, SponsorType type) {
        this.name = name;
        this.material = material;
        this.amount = amount;
        this.type = type;
    }

    public SponsorItem(Material material, int amount, SponsorType type) {
        this(Utils.capitalizeEveryWord(material.name()), material, amount, type);
    }
    
    public String getLoreLine() {
        if (type == SponsorType.FOOD) {
            return amount + " of " + name;
        }
        return name;
    }

    public ItemStack getItemStack() { //TODO
        return null;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public SponsorType getType() {
        return type;
    }
    
    public void setType(SponsorType type) {
        this.type = type;
    }
}
