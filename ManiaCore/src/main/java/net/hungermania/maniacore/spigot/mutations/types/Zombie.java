package net.hungermania.maniacore.spigot.mutations.types;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.hungermania.maniacore.spigot.mutations.DefenseType;
import net.hungermania.maniacore.spigot.mutations.Mutation;
import net.hungermania.maniacore.spigot.mutations.MutationType;
import net.hungermania.maniacore.spigot.util.ArmorSlot;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class Zombie extends Mutation {
    public Zombie() {
        super("Zombie", MutationType.ZOMBIE, 3000, 100, DefenseType.HIGH, 20, DisguiseType.ZOMBIE, Material.ROTTEN_FLESH);
        this.buffs.addAll(Arrays.asList("High Defense Type", "Melee players with a Gold Sword!"));
        this.debuffs.add("Indefinite Slowness I.");
        this.armorSlots.put(ArmorSlot.HELMET, new ItemStack(Material.CHAINMAIL_HELMET));
        this.armorSlots.put(ArmorSlot.CHESTPLATE, new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        this.armorSlots.put(ArmorSlot.LEGGINGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
        this.armorSlots.put(ArmorSlot.BOOTS, new ItemStack(Material.CHAINMAIL_BOOTS));
        this.potionEffects.put(PotionEffectType.SLOW, 0);
        this.inventory.put(0, ItemBuilder.start(Material.GOLD_SWORD).setUnbreakable(true).build());
    }
}
