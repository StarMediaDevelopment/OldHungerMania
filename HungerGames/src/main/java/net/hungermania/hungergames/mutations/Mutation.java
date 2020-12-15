package net.hungermania.hungergames.mutations;

import lombok.Getter;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.hungermania.maniacore.api.MutationType;
import net.hungermania.maniacore.spigot.util.ArmorSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Getter
public abstract class Mutation {
    protected String name;
    protected MutationType type;
    protected int unlockCost, useCost;
    protected DefenseType defenseType;
    protected List<String> buffs = new LinkedList<>(), debuffs = new LinkedList<>();
    protected double maxHP;
    protected Map<Integer, ItemStack> inventory = new HashMap<>();
    protected Map<PotionEffectType, Integer> potionEffects = new HashMap<>();
    protected Map<ArmorSlot, ItemStack> armorSlots = new HashMap<>();
    protected MobDisguise disguise;
    protected Material icon;
    
    public Mutation(String name, MutationType type, int unlockCost, int useCost, DefenseType defenseType, double maxHP, DisguiseType disguiseType, Material material) {
        this.name = name;
        this.type = type;
        this.unlockCost = unlockCost;
        this.useCost = useCost;
        this.defenseType = defenseType;
        this.maxHP = maxHP;
        this.disguise = new MobDisguise(disguiseType, true).setReplaceSounds(true);
        this.disguise.setViewSelfDisguise(false);
        this.icon = material;
    }
    
    public void applyPlayer(Player player) {
        player.setMaxHealth(maxHP);
        player.getInventory().clear();
        for (Entry<Integer, ItemStack> entry : this.inventory.entrySet()) {
            player.getInventory().setItem(entry.getKey(), entry.getValue());
        }
        
        if (!potionEffects.isEmpty()) {
            for (Entry<PotionEffectType, Integer> entry : potionEffects.entrySet()) {
                player.addPotionEffect(new PotionEffect(entry.getKey(), Integer.MAX_VALUE, entry.getValue()));
            }
        }
        
        if (!armorSlots.isEmpty()) {
            ItemStack helmet = armorSlots.get(ArmorSlot.HELMET);
            ItemStack chestplate = armorSlots.get(ArmorSlot.CHESTPLATE);
            ItemStack leggings = armorSlots.get(ArmorSlot.LEGGINGS);
            ItemStack boots = armorSlots.get(ArmorSlot.BOOTS);
            player.getInventory().setHelmet(helmet);
            player.getInventory().setChestplate(chestplate);
            player.getInventory().setLeggings(leggings);
            player.getInventory().setBoots(boots);
        }
        
        Disguise disguise = this.disguise.clone();
        disguise.setEntity(player);
        disguise.startDisguise();
        disguise.getWatcher().setInvisible(false);
        DisguiseAPI.disguiseToAll(player, disguise);
    }
}
