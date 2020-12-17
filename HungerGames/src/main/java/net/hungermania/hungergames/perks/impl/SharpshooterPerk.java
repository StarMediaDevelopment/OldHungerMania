package net.hungermania.hungergames.perks.impl;

import net.hungermania.hungergames.perks.TieredPerk;
import net.hungermania.maniacore.api.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SharpshooterPerk extends TieredPerk {
    public SharpshooterPerk() {
        super("Sharpshooter", 1500, 100, Material.BOW);
        
        this.tiers.put(1, new Tier(1, getBaseCost(), 10) {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                player.getInventory().addItem(new ItemStack(Material.ARROW));
                return true;
            }
        });
    
        this.tiers.put(2, new Tier(2, 3000, 25) {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player == null) return false;
                player.getInventory().addItem(new ItemStack(Material.ARROW));
                return true;
            }
        });
    }
}
