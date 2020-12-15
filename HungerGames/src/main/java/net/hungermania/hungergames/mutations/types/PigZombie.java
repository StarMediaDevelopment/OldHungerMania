package net.hungermania.hungergames.mutations.types;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.hungermania.hungergames.mutations.DefenseType;
import net.hungermania.hungergames.mutations.Mutation;
import net.hungermania.maniacore.api.MutationType;
import net.hungermania.maniacore.spigot.util.ArmorSlot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class PigZombie extends Mutation {
    public PigZombie() {
        super("Pig Zombie", MutationType.PIG_ZOMBIE, 0, 100, DefenseType.LOW, 20, DisguiseType.PIG_ZOMBIE, Material.GOLD_INGOT);
        this.buffs.addAll(Arrays.asList("Charge at your killer with Speed II", "Chop them with a Gold Sword!", "Free for all forever!"));
        this.debuffs.add("No health regeneration.");
        this.armorSlots.put(ArmorSlot.HELMET, new ItemStack(Material.LEATHER_HELMET));
        this.armorSlots.put(ArmorSlot.CHESTPLATE, new ItemStack(Material.LEATHER_CHESTPLATE));
        this.armorSlots.put(ArmorSlot.LEGGINGS, new ItemStack(Material.LEATHER_LEGGINGS));
        this.armorSlots.put(ArmorSlot.BOOTS, new ItemStack(Material.LEATHER_BOOTS));
        this.potionEffects.put(PotionEffectType.SPEED, 1);
        this.potionEffects.put(PotionEffectType.FIRE_RESISTANCE, 0);
        this.inventory.put(0, new ItemStack(Material.GOLD_SWORD));
    }
}