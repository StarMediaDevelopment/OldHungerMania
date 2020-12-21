package net.hungermania.hungergames.perks;

import net.hungermania.hungergames.perks.Perk.PerkCategory;
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
        PERKS.add(SPEED_KILL = new PotionPerk(2000, PotionEffectType.SPEED, 1, 5, Material.SUGAR, PerkCategory.KILL, "Gives you speed for a short time on a kill."));
        PERKS.add(RESISTANCE = new PotionPerk(500, PotionEffectType.DAMAGE_RESISTANCE, 1, 3, Material.ANVIL, PerkCategory.KILL, "Gives you Resistance for a short time on a kill."));
        PERKS.add(REGEN = new PotionPerk(1500, PotionEffectType.REGENERATION, 2, 3, Material.POTION, PerkCategory.KILL, "Gives you regeneration for a short time on a kill."));
        PERKS.add(ASSASSIN = new PotionPerk("Assassin", 5000, PotionEffectType.INVISIBILITY, 1, 5, Material.DIAMOND_SWORD, PerkCategory.KILL, "Gives you invisibility for a short time on a kill."));
        PERKS.add(ABSORPTION = new PotionPerk(5000, PotionEffectType.ABSORPTION, 1, 30, Material.APPLE, PerkCategory.KILL, "Gives you absorption for a short time after a kill."));
        PERKS.add(ENDERMAN = new FlatPerk("Enderman", 1000, 100, Material.ENDER_PEARL, PerkCategory.OTHER, "Completely removes enderpearl teleport damage."));
        PERKS.add(SURVIVALIST = new SurvivalistPerk());
        PERKS.add(MIRACLE = new ItemPerk("Miracle", 1000, 5, new ItemStack(Material.GOLDEN_APPLE), PerkCategory.KILL, "A low chance to get a golden apple on a kill."));
        PERKS.add(SPEED_RACER = new SpeedRacerPerk());
        PERKS.add(FATTY = new FattyPerk());
        PERKS.add(FEATHERWEIGHT = new FeatherweightPerk());
        PERKS.add(SHARPSHOOTER = new SharpshooterPerk());
        PERKS.add(ENCHANT_XP_BOOST = new EnchantXpPerk());
        PERKS.add(BETTY = new BettyPerk());
        PERKS.add(CHEF = new FlatPerk("Chef", 4000, 100, Material.COOKED_BEEF, PerkCategory.OTHER, "When holding an uncooked food item for 7 seconds, it will automatically cook."));
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
