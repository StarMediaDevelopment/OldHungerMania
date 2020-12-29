package net.hungermania.hungergames.perks;

import lombok.Getter;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.records.PerkInfoRecord;
import net.hungermania.hungergames.user.GameUser;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion.Tier;

import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("DuplicatedCode")
public abstract class TieredPerk extends Perk {
    
    protected Map<Integer, Tier> tiers = new TreeMap<>();
    
    public TieredPerk(String name, int baseCost, int chance, Material iconMaterial, PerkCategory category, String description) {
        super(name, baseCost, chance, iconMaterial, category, description);
    }
    
    public Map<Integer, Tier> getTiers() {
        return tiers;
    }
    
    public void setTiers(Map<Integer, Tier> tiers) {
        this.tiers = tiers;
    }
    
    public void handlePurchase(GameUser user) {
        PerkInfo perkInfo = user.getPerkInfo(this);
        Tier tier = getTier(user);
        Tier nextTier;
        if (tier == null) {
            nextTier = this.tiers.get(1);
        } else {
            nextTier = this.tiers.get(tier.getNumber() + 1);
        }
        if (nextTier == null) {
            user.sendMessage("&cYou have already purchased the max tier of that perk.");
            return;
        }
    
        if (user.getStat(Stats.COINS).getValueAsInt() < nextTier.getCost()) {
            user.sendMessage("&cYou do not have enough coins to purchase that perk.");
            return;
        }
    
        user.getStat(Stats.COINS).setValue(user.getStat(Stats.COINS).getValueAsInt() - nextTier.getCost());
        perkInfo.setValue(true);
        perkInfo.getUnlockedTiers().add(nextTier.getNumber());
        ManiaCore.getInstance().getDatabase().pushRecord(new PerkInfoRecord(perkInfo));
        user.sendMessage("&aYou purchased Tier " + nextTier.getNumber() + " of the perk " + getDisplayName());
        Redis.pushUser(user);
    }
    
    public ItemStack getIcon(GameUser user) {
        if (iconMaterial == null) {
            HungerGames.getInstance().getLogger().severe("Perk " + getName() + " does not have an icon setup.");
            return ItemBuilder.start(Material.REDSTONE_BLOCK).withLore("&cNo Icon Setup.").build();
        }
        ItemBuilder itemBuilder = ItemBuilder.start(iconMaterial).setDisplayName("&b" + displayName);
        List<String> lore = new LinkedList<>();
        PerkInfo perkInfo = user.getPerkInfo(this);
        Set<Integer> unlockedTiers = perkInfo.getUnlockedTiers();
        if (perkInfo.getValue()) {
            lore.add(Utils.color("&a&oPurchased"));
            lore.add("&7&o" + getDescription());
            generatePerkLore(user, lore, unlockedTiers);
    
            if (perkInfo.isActive()) {
                lore.add("&a&lSELECTED");
            } else {
                lore.add("&6&lRight Click &fto select.");
            }
        } else {
            Tier current = null;
            for (Integer t : unlockedTiers) {
                if (current == null) {
                    current = this.tiers.get(t);
                } else {
                    if (t > current.getNumber()) {
                        current = this.tiers.get(t);
                    }
                }
            }
            
            if (user.getStat(Stats.COINS).getValueAsInt() >= baseCost) {
                lore.add(Utils.color("&e&oAvailable"));
                lore.add("&7&o" + getDescription());
                generatePerkLore(user, lore, unlockedTiers);
                
                int tierNumber = 0, cost = 0;
                if (current == null) {
                    tierNumber = 1;
                    cost = getBaseCost();
                } else {
                    if (this.tiers.get(tierNumber + 1) != null) {
                        Tier nextTier = this.tiers.get(tierNumber + 1);
                        tierNumber = nextTier.getNumber();
                        cost = nextTier.getCost();
                    }
                }
                if (tierNumber != 0) {
                    lore.add("&6&lLeft Click &fto purchase tier " + tierNumber + " for " + cost + " coins.");
                } else {
                    lore.add("&cYou have already purchased the max teir of this perk.");
                }
            } else {
                lore.add(Utils.color("&c&oLocked"));
                lore.add("&dYou do not have enough coins to purchase this perk.");
            }
        }
    
        itemBuilder.setLore(lore);
        return itemBuilder.build();
    }
    
    private void generatePerkLore(GameUser user, List<String> lore, Set<Integer> unlockedTiers) {
        lore.add("");
        
        lore.add("&d&lTIERS");
        for (Entry<Integer, Tier> entry : this.tiers.entrySet()) {
            String color;
            if (unlockedTiers.contains(entry.getKey())) {
                color = "&a";
            } else {
                if (user.getStat(Stats.COINS).getValueAsInt() >= entry.getValue().getCost()) {
                    color = "&e";
                } else {
                    color = "&c";
                }
            }
            
            lore.add(" &8- " + color + " Tier " + entry.getKey() + ": " + entry.getValue().getDescription());
        }
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
        private String description;
        public Tier(int number, int cost, String description) {
            this.number = number;
            this.cost = cost;
            this.chance = 100;
            this.description = description;
        }
        
        public Tier(int number, int cost, int chance, String description) {
            this.number = number;
            this.cost = cost;
            this.chance = chance;
            this.description = description;
        }
    
        public abstract boolean activate(User user);
    }
}