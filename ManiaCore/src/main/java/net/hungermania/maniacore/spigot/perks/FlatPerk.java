package net.hungermania.maniacore.spigot.perks;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.Material;

public class FlatPerk extends Perk {
    public FlatPerk() {
    }
    
    public FlatPerk(String name, int baseCost, int chance, Material iconMaterial, PerkCategory category, String description) {
        super(name, baseCost, chance, iconMaterial, category, description);
    }
    
    public boolean activate(SpigotUser user) {
        if (user.getPerkInfo(this).getValue()) {
            return ManiaCore.RANDOM.nextInt(100) <= this.chance;
        }
        return false;
    }
}
