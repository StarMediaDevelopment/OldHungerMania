package net.hungermania.hungergames.perks.impl;

import net.hungermania.hungergames.perks.TieredPerk;
import net.hungermania.maniacore.api.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FattyPerk extends TieredPerk {
    public FattyPerk() {
        super("Fatty", 1000, 100, Material.CAKE, PerkCategory.OTHER);
        
        this.tiers.put(1, new Tier(1, getBaseCost(), 10) {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                if (player.getHealth() < player.getMaxHealth()) {
                    player.setHealth(player.getHealth() + 1);
                }
                return true;
            }
        });
    
        this.tiers.put(2, new Tier(2, 2000, 20) {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                if (player.getHealth() < player.getMaxHealth()) {
                    player.setHealth(player.getHealth() + 1);
                }
                return true;
            }
        });
    
        this.tiers.put(3, new Tier(3, 3000, 25) {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                if (player.getHealth() < player.getMaxHealth()) {
                    player.setHealth(player.getHealth() + 1);
                }
                return true;
            }
        });
    }
}
