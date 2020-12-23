package net.hungermania.hungergames.perks;

import lombok.Getter;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.user.GameUser;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public abstract class TieredPerk extends Perk {
    
    protected Map<Integer, Tier> tiers = new HashMap<>();
    
    public TieredPerk(String name, int baseCost, int chance, Material iconMaterial, PerkCategory category, String description) {
        super(name, baseCost, chance, iconMaterial, category, description);
    }
    
    public Map<Integer, Tier> getTiers() {
        return tiers;
    }
    
    public void setTiers(Map<Integer, Tier> tiers) {
        this.tiers = tiers;
    }
    
    public ItemStack getIcon(GameUser user) {
        if (iconMaterial == null) {
            HungerGames.getInstance().getLogger().severe("Perk " + getName() + " does not have an icon setup.");
            return ItemBuilder.start(Material.REDSTONE_BLOCK).withLore("&cNo Icon Setup.").build();
        }
        ItemBuilder itemBuilder = ItemBuilder.start(iconMaterial).setDisplayName("&b" + displayName);
        List<String> lore = new LinkedList<>();
        PerkInfo perkInfo = user.getPerkInfo(this);
        if (perkInfo.getValue()) {
            lore.add(Utils.color("&a&oPurchased"));
            lore.add("&7&o" + getDescription());
            if (perkInfo.isActive()) {
                lore.add("");
                lore.add("&a&lSELECTED");
            } else {
                lore.add("");
                lore.add("&6&lRight Click &fto select.");
            }
        } else {
            if (user.getStat(Stats.COINS).getValueAsInt() >= baseCost) {
                lore.add(Utils.color("&e&oAvailable"));
                lore.add("&7&o" + getDescription());
                lore.add("");
                lore.add("&6&lLeft Click &fto purchase for " + getBaseCost() + ".");
            } else {
                lore.add(Utils.color("&c&oLocked"));
                lore.add("&dYou do not have enough coins to purchase this perks.");
            }
        }
    
        itemBuilder.setLore(lore);
        return itemBuilder.build();
    }
    
    public boolean activate(GameUser user) {
        if (!user.getPerkInfo(this).getValue()) return false;
        Tier tier = getTier(user);
        if (tier == null) return false;
        return ManiaCore.RANDOM.nextInt(100) <= tier.getChance() && tier.activate(user);
    }
    
    public Tier getTier(GameUser user) {
        int currentTier = 0;
        for (Entry<String, Statistic> stats : user.getStats().entrySet()) {
            if (stats.getKey().contains(getName())) {
                int t = Integer.parseInt(stats.getKey().split(":")[1]);
                if (t > currentTier) {
                    currentTier = t;
                }
            }
        }
        return this.tiers.get(currentTier);
    }
    
    @Getter
    public static abstract class Tier {
        private int number, cost, chance;
        public Tier(int number, int cost) {
            this.number = number;
            this.cost = cost;
            this.chance = 100;
        }
        
        public Tier(int number, int cost, int chance) {
            this.number = number;
            this.cost = cost;
            this.chance = chance;
        }
    
        public abstract boolean activate(User user);
    }
}