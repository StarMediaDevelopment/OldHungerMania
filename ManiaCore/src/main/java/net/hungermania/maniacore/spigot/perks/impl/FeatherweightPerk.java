package net.hungermania.maniacore.spigot.perks.impl;

import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.spigot.perks.TieredPerk;
import org.bukkit.Material;

public class FeatherweightPerk extends TieredPerk {
    public FeatherweightPerk() {
        super("Featherweight", 1500, 100, Material.FEATHER, PerkCategory.OTHER, "You have a chance to take less fall damage.");
        
        this.tiers.put(1, new Tier(1, getBaseCost(), 10, "10% chance to take 15% less fall damage.") {
            public boolean activate(User user) {
                return true;
            }
        });
    
        this.tiers.put(2, new Tier(2, 3000, 20, "20% chance to take 25% less fall damage.") {
            public boolean activate(User user) {
                return true;
            }
        });
    
        this.tiers.put(3, new Tier(3, 5000, 25, "25% chance to take 50% less fall damage.") {
            public boolean activate(User user) {
                return true;
            }
        });
    }
}
