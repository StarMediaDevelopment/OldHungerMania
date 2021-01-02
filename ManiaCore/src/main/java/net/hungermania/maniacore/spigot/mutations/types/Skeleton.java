package net.hungermania.maniacore.spigot.mutations.types;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.hungermania.maniacore.spigot.mutations.DefenseType;
import net.hungermania.maniacore.spigot.mutations.Mutation;
import net.hungermania.maniacore.spigot.mutations.MutationType;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class Skeleton extends Mutation {
    public Skeleton() {
        super("Skeleton", MutationType.SKELETON, 3000, 100, DefenseType.NONE, 20, DisguiseType.SKELETON, Material.BOW);
        this.buffs.addAll(Arrays.asList("Charge at your killer with Speed I.", "Get those 360 no-scopes with a Bow!", "Wooden Sword for those intense melee fights."));
        this.debuffs.add("Take 50% amplified damage");
        this.potionEffects.put(PotionEffectType.SPEED, 0);
        this.inventory.put(0, new ItemStack(Material.WOOD_SWORD));
        this.inventory.put(1, ItemBuilder.start(Material.BOW).build());
        this.inventory.put(8, new ItemStack(Material.ARROW, 64));
    }
}
