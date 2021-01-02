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

public class Creeper extends Mutation {
    public Creeper() {
        super("Creeper", MutationType.CREEPER, 3000, 100, DefenseType.MEDIUM, 10, DisguiseType.CREEPER, Material.SULPHUR);
        this.buffs.addAll(Arrays.asList("Medium Defense Type", "Regeneration", "Play with Explosive TNT!", "Suicide Mode", "Speed I", "Immune to Explosion Damage"));
        this.debuffs.add("10 HP Maximum");
        this.armorSlots.put(ArmorSlot.HELMET, new ItemStack(Material.CHAINMAIL_HELMET));
        this.armorSlots.put(ArmorSlot.CHESTPLATE, new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        this.armorSlots.put(ArmorSlot.LEGGINGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
        this.armorSlots.put(ArmorSlot.BOOTS, new ItemStack(Material.CHAINMAIL_BOOTS));
        this.potionEffects.put(PotionEffectType.SPEED, 0);
        this.inventory.put(0, new ItemStack(Material.TNT, 64));
        this.inventory.put(1, ItemBuilder.start(Material.SULPHUR).setDisplayName("&fSuicide").build());
    }
}
