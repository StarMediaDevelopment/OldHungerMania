package net.hungermania.maniacore.spigot.perks.impl;

import net.hungermania.maniacore.spigot.perks.TieredPerk;
import net.hungermania.maniacore.api.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class EnchantXpPerk extends TieredPerk {
    
    public EnchantXpPerk() {
        super("Enchanting XP Level Boost", 500, 100, Material.EXP_BOTTLE, PerkCategory.KILL, "Gives you enchantment xp on a kill.");
        
        this.tiers.put(1, new Tier(1, getBaseCost(), "Get 1 enchantment level on a kill.") {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                player.setLevel(player.getLevel() + 1);
                return true;
            }
        });
        this.tiers.put(2, new Tier(2, getBaseCost(), "Get 2 enchantment levels on a kill.") {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                player.setLevel(player.getLevel() + 2);
                return true;
            }
        });
        this.tiers.put(3, new Tier(3, getBaseCost(), "Get 3 enchantment levels on a kill.") {
            public boolean activate(User user) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                player.setLevel(player.getLevel() + 3);
                return true;
            }
        });
    }
}
