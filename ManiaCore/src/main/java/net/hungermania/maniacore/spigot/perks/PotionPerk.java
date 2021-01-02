package net.hungermania.maniacore.spigot.perks;

import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionPerk extends FlatPerk {
    
    protected PotionEffectType type;
    protected int amplifier, length;
    
    public PotionPerk(int cost, PotionEffectType type, int amplifier, int length, Material icon, PerkCategory category, String description) {
        this.displayName = type.getName() + " " + amplifier;
        this.baseCost = cost;
        this.chance = 100;
        this.type = type;
        this.amplifier = amplifier;
        this.length = length;
        this.iconMaterial = icon;
        this.category = category;
        this.description = description;
    }
    
    public PotionPerk(String name, int cost, PotionEffectType type, int amplifier, int length, Material icon, PerkCategory category, String description) {
        this.displayName = name;
        this.baseCost = cost;
        this.chance = 100;
        this.type = type;
        this.amplifier = amplifier;
        this.length = length;
        this.iconMaterial = icon;
        this.category = category;
        this.description = description;
    }
    
    public ItemStack getIcon(SpigotUser user) {
        if (this.iconMaterial == Material.POTION) {
            ItemStack itemStack = super.getIcon(user);
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.setMainEffect(this.type);
            itemStack.setItemMeta(potionMeta);
            return itemStack;
        } else {
            return super.getIcon(user);
        }
    }
    
    public boolean activate(SpigotUser user) {
        if (user.getPerkInfo(this).getValue()) {
            Player player = user.getBukkitPlayer();
            if (player == null) return false;
            player.addPotionEffect(new PotionEffect(type, length * 20, amplifier - 1));
            return true;
        }
        return false;
    }
}
