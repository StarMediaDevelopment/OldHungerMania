package net.hungermania.hungergames.mutations.types;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.hungermania.hungergames.mutations.DefenseType;
import net.hungermania.hungergames.mutations.Mutation;
import net.hungermania.maniacore.api.MutationType;
import net.hungermania.maniacore.spigot.util.ArmorSlot;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class Enderman extends Mutation {
    public Enderman() {
        super("Enderman", MutationType.ENDERMAN, 5000, 100, DefenseType.NONE, 20, DisguiseType.ENDERMAN, Material.ENDER_PEARL);
        this.buffs.addAll(Arrays.asList("Teleport where you are looking!", "No fall damage!", "Melee players with a Knockback II gold sword!"));
        this.debuffs.add("Receive damage while in water.");
        this.inventory.put(0, ItemBuilder.start(Material.GOLD_SWORD).addEnchantment(Enchantment.KNOCKBACK, 1).build());
        this.inventory.put(1, new ItemStack(Material.ENDER_PEARL, 32));
        this.potionEffects.put(PotionEffectType.WEAKNESS, 1);
        this.armorSlots.put(ArmorSlot.HELMET, new ItemStack(Material.LEATHER_HELMET));
        this.armorSlots.put(ArmorSlot.CHESTPLATE, new ItemStack(Material.LEATHER_CHESTPLATE));
        this.armorSlots.put(ArmorSlot.LEGGINGS, new ItemStack(Material.LEATHER_LEGGINGS));
        this.armorSlots.put(ArmorSlot.BOOTS, new ItemStack(Material.LEATHER_BOOTS));
    }
}