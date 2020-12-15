package net.hungermania.hungergames.perks.impl;

import net.hungermania.hungergames.perks.TieredPerk;
import net.hungermania.maniacore.api.user.User;
import org.bukkit.Material;

public class FeatherweightPerk extends TieredPerk {
    public FeatherweightPerk() {
        super("Featherweight", 1500, 100, Material.FEATHER);
        
        this.tiers.put(1, new Tier(1, getBaseCost(), 10) {
            public boolean activate(User user) {
                return true;
            }
        });
    
        this.tiers.put(2, new Tier(2, 3000, 20) {
            public boolean activate(User user) {
                return true;
            }
        });
    
        this.tiers.put(3, new Tier(5000, 25) {
            public boolean activate(User user) {
                return true;
            }
        });
    }
}
