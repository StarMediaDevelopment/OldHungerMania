package net.hungermania.hungergames.perks.impl;

import net.hungermania.hungergames.perks.FlatPerk;
import net.hungermania.hungergames.user.GameUser;
import net.hungermania.maniacore.spigot.util.NBTWrapper;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public class BettyPerk extends FlatPerk {
    public BettyPerk() {
        super("Betty", 1000, 100, Material.DIAMOND_AXE);
    }
    
    private static final Set<Material> AXE_TYPES = EnumSet.of(Material.WOOD_AXE, Material.STONE_AXE, Material.GOLD_AXE, Material.IRON_AXE, Material.DIAMOND_AXE);
    
    public boolean activate(GameUser user) {
        ItemStack hand = user.getBukkitPlayer().getItemInHand();
        if (hand == null) { return false; }
        if (!AXE_TYPES.contains(hand.getType())) { return false; }
        String rawKills;
        try {
            rawKills = NBTWrapper.getNBTString(hand, "kills");
        } catch (Exception e) {
            return false;
        }
    
        if (rawKills == null || rawKills.equals("")) { return false; }
        
        Map<UUID, Integer> kills = new HashMap<>();
        for (String s : rawKills.split(";")) {
            if (s != null) {
                String[] split = s.split(":");
                try {
                    UUID uuid = UUID.fromString(split[0]);
                    Integer integer = Integer.parseInt(split[1]);
                    kills.put(uuid, integer);
                } catch (Exception e) {}
            }
        }
    
        if (!kills.containsKey(user.getUniqueId())) { return false; }
        if (kills.get(user.getUniqueId()) >= 3) {
            if (hand.getEnchantmentLevel(Enchantment.DAMAGE_ALL) > 0) { return false; }
            hand.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        }
        kills.put(user.getUniqueId(), kills.get(user.getUniqueId()) + 1);
        StringBuilder sb = new StringBuilder();
        for (Entry<UUID, Integer> entry : kills.entrySet()) {
            sb.append(entry.getKey().toString()).append(":").append(entry.getValue()).append(";");
        }
        try {
            NBTWrapper.addNBTString(hand, "kills", sb.toString());
        } catch (Exception e) {
            return false;
        }
    
        return true;
    }
}
