package net.hungermania.hungergames.loot;

import lombok.Getter;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.maniacore.api.ManiaCore;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiamondLoot extends Loot {
    
    @Getter private List<Loot> loot = new ArrayList<>();
    private List<Loot> lootChances = new ArrayList<>();
    
    private static final Loot DIAMOND = new Loot(Material.DIAMOND, 80);
    
    public DiamondLoot() {
        super(DIAMOND.getMaterial(), 3);
        this.loot.add(DIAMOND);
        this.loot.add(new Loot(Material.DIAMOND_SWORD, 20));
        this.loot.add(new Loot(Material.DIAMOND_AXE, 20));
        this.loot.add(new Loot(Material.DIAMOND_HELMET, 15));
        this.loot.add(new Loot(Material.DIAMOND_CHESTPLATE, 12));
        this.loot.add(new Loot(Material.DIAMOND_LEGGINGS, 13));
        this.loot.add(new Loot(Material.DIAMOND_BOOTS, 16));

        for (Loot l : this.loot) {
            for (int c = 0; c < l.getWeight(); c++) {
                lootChances.add(l);
            }
        }

        Collections.shuffle(lootChances);
    }

    public ItemStack generateItemStack() {
        Game game = HungerGames.getInstance().getGameManager().getCurrentGame();
        if (game == null) {
            return DIAMOND.generateItemStack();
        }
        
        if (game.isDiamondSpecial()) {
            return lootChances.get(ManiaCore.RANDOM.nextInt(lootChances.size())).generateItemStack();
        }
        
        return DIAMOND.generateItemStack();
    }

    public int getWeight() {
        return 3;
    }
}
