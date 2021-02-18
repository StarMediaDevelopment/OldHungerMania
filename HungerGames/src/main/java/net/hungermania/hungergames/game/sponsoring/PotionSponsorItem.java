package net.hungermania.hungergames.game.sponsoring;

import net.hungermania.manialib.util.Utils;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

public class PotionSponsorItem extends SponsorItem {
    
    private PotionEffectType type;
    private int seconds, amplifier;
    
    public PotionSponsorItem(PotionEffectType effect, int amplifier, int seconds) {
        super(Utils.capitalizeEveryWord(effect.getName()), Material.POTION, 1, SponsorType.POTION);
    }

    public PotionSponsorItem(String name, PotionEffectType effect, int amplifier, int seconds) {
        super(name, Material.POTION, 1, SponsorType.POTION);
    }

    public String getLoreLine() {
        return Utils.capitalizeEveryWord(getName()) + Utils.romanNumerals(amplifier) + " for " + seconds + " seconds";
    }
}
