package net.hungermania.hungergames.perks.impl;

import net.hungermania.hungergames.perks.TieredPerk;
import net.hungermania.maniacore.api.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedRacerPerk extends TieredPerk {
    public SpeedRacerPerk() {
        super("Speed Racer", 1500, 100, Material.BEACON, PerkCategory.OTHER);
        
        this.tiers.put(1, new Tier(1, getBaseCost()) {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 20));
                return true;
            }
        });
    
        this.tiers.put(2, new Tier(2, 2000) {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 40));
                return true;
            }
        });
    
        this.tiers.put(3, new Tier(3, 2500) {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 60));
                return true;
            }
        });
    }
}
