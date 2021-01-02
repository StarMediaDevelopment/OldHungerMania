package net.hungermania.maniacore.spigot.perks;

import lombok.Getter;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import net.hungermania.manialib.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
    
    public void handlePurchase(SpigotUser user) {
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
    
    public ItemStack getIcon(SpigotUser user) {
        if (iconMaterial == null) {
            ManiaCore.getInstance().getLogger().severe("Perk " + getName() + " does not have an icon setup.");
            return ItemBuilder.start(Material.REDSTONE_BLOCK).withLore("&cNo Icon Setup.").build();
        }
        ItemBuilder itemBuilder = ItemBuilder.start(iconMaterial).setDisplayName("&b" + displayName);
        List<String> lore = new LinkedList<>(), tierLore = new LinkedList<>();
        PerkInfo perkInfo = user.getPerkInfo(this);
        Set<Integer> unlockedTiers = perkInfo.getUnlockedTiers();
        PerkStatus status;
        Statistic coins = user.getStat(Stats.COINS);
        
        Tier current = null, nextTier = null;
        for (Integer t : unlockedTiers) {
            if (current == null) {
                current = this.tiers.get(t);
            } else {
                if (t > current.getNumber()) {
                    current = this.tiers.get(t);
                }
            }
        }
        
        if (current == null) {
            nextTier = this.tiers.get(1);
        } else {
            if (this.tiers.get(current.getNumber() + 1) != null) {
                nextTier = this.tiers.get(current.getNumber() + 1);
            }
        }
        
        tierLore.add("&d&lTIERS");
        for (Entry<Integer, Tier> entry : this.tiers.entrySet()) {
            String color;
            if (unlockedTiers.contains(entry.getKey())) {
                color = "&a";
            } else {
                if (coins.getValueAsInt() >= entry.getValue().getCost()) {
                    color = "&e";
                } else {
                    color = "&c";
                }
            }
            
            tierLore.add(" &8- " + color + " Tier " + entry.getKey() + ": " + entry.getValue().getDescription());
        }
        
        if (this.tiers.size() == unlockedTiers.size()) {
            status = PerkStatus.PURCHASED;
        } else if ((unlockedTiers.size() > 0) && unlockedTiers.size() < this.tiers.size()) {
            status = PerkStatus.PARTIALLY_PURCHASED;
        } else if (current == null) {
            if (coins.getValueAsInt() >= nextTier.getCost()) {
                status = PerkStatus.AVAILABLE;
            } else {
                status = PerkStatus.LOCKED;
            }
        } else if (nextTier != null) {
            if (coins.getValueAsInt() >= nextTier.getCost()) {
                status = PerkStatus.AVAILABLE;
            } else {
                status = PerkStatus.PARTIALLY_PURCHASED;
            }
        } else {
            status = PerkStatus.LOCKED;
        }
        
        String rightClickLore = "", leftClickLore = "";
        lore.add(status.getColor() + Utils.capitalizeEveryWord(status.name()));
        if (getDescription().contains("\n")) {
            String[] lines = getDescription().split("\n");
            for (String line : lines) {
                lore.add("&7&o" + line);
            }
        } else {
            lore.add("&7&o" + getDescription());
        }
        
        lore.add("");
        lore.addAll(tierLore);
        lore.add("");
        
        if (perkInfo.isActive()) {
            lore.add("&a&lSELECTED");
        } else {
            rightClickLore = "&6&lRight Click &fto select this perk.";
        }
        
        if (status == PerkStatus.AVAILABLE || status == PerkStatus.PARTIALLY_PURCHASED) {
            leftClickLore = "&6&lLeft Click &fto purchase Tier " + nextTier.getNumber() + " for " + nextTier.getCost() + " coins.";
        }
        
        if (!leftClickLore.equals("")) {
            lore.add(leftClickLore);
        }
        
        if (!rightClickLore.equals("")) {
            lore.add(rightClickLore);
        }
        
        itemBuilder.setLore(lore);
        return itemBuilder.build();
    }
    
    public boolean activate(SpigotUser user) {
        if (!user.getPerkInfo(this).getValue()) { return false; }
        Tier tier = getTier(user);
        if (tier == null) { return false; }
        return ManiaCore.RANDOM.nextInt(100) <= tier.getChance() && tier.activate(user);
    }
    
    public Tier getTier(SpigotUser user) {
        int currentTier = 0;
        PerkInfo perkInfo = user.getPerkInfo(this);
        if (perkInfo != null) {
            Set<Integer> unlockedTiers = perkInfo.getUnlockedTiers();
            for (Integer unlockedTier : unlockedTiers) {
                if (unlockedTier > currentTier) {
                    currentTier = unlockedTier;
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