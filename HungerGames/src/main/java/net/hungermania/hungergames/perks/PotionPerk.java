package net.hungermania.hungergames.perks;

import net.hungermania.hungergames.user.GameUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionPerk extends FlatPerk {
    
    protected PotionEffectType type;
    protected int amplifier, length;
    
    public PotionPerk(int cost, PotionEffectType type, int amplifier, int length) {
        this.displayName = type.getName() + " " + amplifier;
        this.baseCost = cost;
        this.chance = 100;
        this.type = type;
        this.amplifier = amplifier;
        this.length = length;
        this.iconMaterial = Material.POTION;
    }
    
    public PotionPerk(String name, int cost, PotionEffectType type, int amplifier, int length) {
        this.displayName = name;
        this.baseCost = cost;
        this.chance = 100;
        this.type = type;
        this.amplifier = amplifier;
        this.length = length;
        this.iconMaterial = Material.POTION;
    }
    
    public boolean activate(GameUser user) {
        if (user.getPerkInfo(this).getValue()) {
            Player player = user.getBukkitPlayer();
            if (player == null) return false;
            player.addPotionEffect(new PotionEffect(type, length * 20, amplifier - 1));
            return true;
        }
        return false;
    }
}
