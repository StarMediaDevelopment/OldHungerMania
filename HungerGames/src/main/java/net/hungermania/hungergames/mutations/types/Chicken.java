package net.hungermania.hungergames.mutations.types;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.hungermania.hungergames.mutations.DefenseType;
import net.hungermania.hungergames.mutations.Mutation;
import net.hungermania.maniacore.api.MutationType;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Chicken extends Mutation {
    public Chicken() {
        super("Chicken", MutationType.CHICKEN, 5000, 100, DefenseType.NONE, 6, DisguiseType.CHICKEN, Material.EGG);
        this.buffs.addAll(Arrays.asList("Launch high into the air! &c&lWIP", "Use your brethren as a parachute! &c&lWIP", "No fall damage!", "Shoot eggs! &c&lWIP"));
        this.debuffs.addAll(Arrays.asList("Less health", "No armor"));
        // TODO https://bukkit.org/threads/launch-players-into-the-air.60946/
        this.inventory.put(0, new ItemStack(Material.WOOD_SWORD));
        this.inventory.put(1, ItemBuilder.start(Material.SLIME_BALL).setDisplayName("&fLaunch &c&lWIP").build());
        this.inventory.put(2, ItemBuilder.start(Material.FEATHER).setDisplayName("&fParachute &c&lWIP").build());
    }
}
