package net.hungermania.hungergames.perks;

import net.hungermania.hungergames.perks.impl.*;
import net.hungermania.hungergames.user.GameUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class Perks {
    
    public static final Set<Perk> PERKS = new HashSet<>();
    public static final Perk CHEF, ENCHANT_XP_BOOST, SPEED_KILL, RESISTANCE, REGEN, ASSASSIN, ABSORPTION, ENDERMAN, SURVIVALIST, MIRACLE, SPEED_RACER, FATTY, FEATHERWEIGHT, SHARPSHOOTER, BETTY;
    
    static {
        PERKS.add(SPEED_KILL = new PotionPerk(2000, PotionEffectType.SPEED, 1, 5));
        PERKS.add(RESISTANCE = new PotionPerk(500, PotionEffectType.DAMAGE_RESISTANCE, 1, 3));
        PERKS.add(REGEN = new PotionPerk(1500, PotionEffectType.REGENERATION, 2, 3));
        PERKS.add(ASSASSIN = new PotionPerk("Assassin", 5000, PotionEffectType.INVISIBILITY, 1, 5));
        PERKS.add(ABSORPTION = new PotionPerk(5000, PotionEffectType.ABSORPTION, 1, 30));
        PERKS.add(ENDERMAN = new FlatPerk("Enderman", 1000, 100, Material.ENDER_PEARL));
        PERKS.add(SURVIVALIST = new FlatPerk("Survivalist", 10000, 1, Material.GOLDEN_APPLE) {
            public boolean activate(GameUser user) {
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
        });
        PERKS.add(MIRACLE = new ItemPerk("Miracle", 1000, 5, new ItemStack(Material.GOLDEN_APPLE)));
        PERKS.add(SPEED_RACER = new SpeedRacerPerk());
        PERKS.add(FATTY = new FattyPerk());
        PERKS.add(FEATHERWEIGHT = new FeatherweightPerk());
        PERKS.add(SHARPSHOOTER = new SharpshooterPerk());
        PERKS.add(ENCHANT_XP_BOOST = new EnchantXpPerk());
        PERKS.add(BETTY = new BettyPerk());
        PERKS.add(CHEF = new FlatPerk("Chef", 4000, 100, Material.COOKED_BEEF));
    }
    
    public static Perk getPerk(String name) {
        for (Perk perk : PERKS) {
            if (perk.getName().replace(" ", "_").equalsIgnoreCase(name)) {
                return perk;
            }
        }
        return null;
    }
}
