package net.hungermania.hungergames.perks;

import net.hungermania.hungergames.perks.impl.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.TreeSet;

public class Perks {
    
    public static final Set<Perk> PERKS = new TreeSet<>();
    public static final Perk CHEF, ENCHANT_XP_BOOST, SPEED_KILL, RESISTANCE, REGEN, ASSASSIN, ABSORPTION, ENDERMAN, SURVIVALIST, MIRACLE, SPEED_RACER, FATTY, FEATHERWEIGHT, SHARPSHOOTER, BETTY;
    
    static {
        PERKS.add(SPEED_KILL = new PotionPerk(2000, PotionEffectType.SPEED, 1, 5, Material.SUGAR));
        PERKS.add(RESISTANCE = new PotionPerk(500, PotionEffectType.DAMAGE_RESISTANCE, 1, 3, Material.ANVIL));
        PERKS.add(REGEN = new PotionPerk(1500, PotionEffectType.REGENERATION, 2, 3, Material.POTION));
        PERKS.add(ASSASSIN = new PotionPerk("Assassin", 5000, PotionEffectType.INVISIBILITY, 1, 5, Material.DIAMOND_SWORD));
        PERKS.add(ABSORPTION = new PotionPerk(5000, PotionEffectType.ABSORPTION, 1, 30, Material.APPLE));
        PERKS.add(ENDERMAN = new FlatPerk("Enderman", 1000, 100, Material.ENDER_PEARL));
        PERKS.add(SURVIVALIST = new SurvivalistPerk());
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
