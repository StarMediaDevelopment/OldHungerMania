package net.hungermania.maniacore.spigot.perks.impl;

import net.hungermania.maniacore.spigot.perks.FlatPerk;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SurvivalistPerk extends FlatPerk {
    public SurvivalistPerk() {
        super("Survivalist", 10000, 1, Material.GOLD_HELMET, PerkCategory.KILL, "A low chance to gain a great boost after a kill.");
    }
    
    public boolean activate(SpigotUser user) {
        if (super.activate(user)) {
            Player player = user.getBukkitPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 4, 100));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 0, 100));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 0, 100));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2, 100));
            return true;
        }
        
        return false;
    }
}
