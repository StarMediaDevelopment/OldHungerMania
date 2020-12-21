package net.hungermania.hungergames.perks;

import net.hungermania.hungergames.user.GameUser;
import net.hungermania.maniacore.api.ManiaCore;
import org.bukkit.Material;

public class FlatPerk extends Perk {
    public FlatPerk() {
    }
    
    public FlatPerk(String name, int baseCost, int chance, Material iconMaterial, PerkCategory category, String description) {
        super(name, baseCost, chance, iconMaterial, category, description);
    }
    
    public boolean activate(GameUser user) {
        if (user.getPerkInfo(this).getValue()) {
            return ManiaCore.RANDOM.nextInt(100) <= this.chance;
        }
        return false;
    }
}
