package net.hungermania.hungergames.perks;

import lombok.Getter;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.records.PerkInfoRecord;
import net.hungermania.hungergames.user.GameUser;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public abstract class Perk implements Comparable<Perk> {
    
    protected String displayName;
    protected int baseCost;
    protected int chance;
    protected Material iconMaterial;
    protected PerkCategory category;
    protected String description;
    
    public Perk() {
    }
    
    public Perk(String displayName, int baseCost, int chance, Material iconMaterial, PerkCategory category, String description) {
        this.displayName = displayName;
        this.baseCost = baseCost;
        this.chance = chance;
        this.iconMaterial = iconMaterial;
        this.category = category;
        this.description = description;
    }
    
    public abstract boolean activate(GameUser user);
    
    public PerkInfo create(UUID uuid) {
        return new PerkInfo(uuid, getName(), getDefaultValue(), System.currentTimeMillis(), System.currentTimeMillis());
    }
    
    public final String getName() {
        return this.displayName.toLowerCase().replace(" ", "_");
    }
    
    public final boolean getDefaultValue() {
        return false;
    }
    
    public final boolean isNumber() {
        return false;
    }
    
    public void handlePurchase(GameUser user) {
        PerkInfo perkInfo = user.getPerkInfo(this);
        if (perkInfo.getValue()) {
            user.sendMessage("&cYou have already purchased that perk.");
            return;
        }
        
        if (user.getStat(Stats.EXPERIENCE).getValueAsInt() < this.baseCost) {
            user.sendMessage("&cYou do not have enough coins to purchase that perk.");
            return;
        }
        
        user.getStat(Stats.COINS).setValue(user.getStat(Stats.COINS).getValueAsInt() - this.baseCost);
        perkInfo.setValue(true);
        ManiaCore.getInstance().getDatabase().pushRecord(new PerkInfoRecord(perkInfo));
        user.sendMessage("&aYou purchased the perk " + getDisplayName());
        Redis.pushUser(user);
    }
    
    public ItemStack getIcon(GameUser user) {
        if (iconMaterial == null) {
            HungerGames.getInstance().getLogger().severe("Perk " + getName() + " does not have an icon setup.");
            return ItemBuilder.start(Material.REDSTONE_BLOCK).withLore("&cNo Icon Setup.").build();
        }
        ItemBuilder itemBuilder = ItemBuilder.start(iconMaterial).setDisplayName("&b" + displayName);
        List<String> lore = new LinkedList<>();
        if (user.getPerkInfo(this).getValue()) {
            lore.add(Utils.color("&a&oPurchased"));
            lore.add("&7&o" + getDescription());
            if (user.getPerkInfo(this).isActive()) {
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
    
    public int compareTo(Perk o) {
        return this.getName().compareTo(o.getName());
    }
    
    public enum PerkCategory {
        KILL(Material.DIAMOND_SWORD), OTHER(Material.BEDROCK);
        private Material iconMaterial;
        PerkCategory(Material iconMaterial) {
            this.iconMaterial = iconMaterial;
        }
        
        public ItemStack getIcon() {
            return ItemBuilder.start(iconMaterial).setDisplayName("&d" + Utils.capitalizeEveryWord(name() + " Perks")).build();
        }
    }
}