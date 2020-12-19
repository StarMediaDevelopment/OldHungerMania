package net.hungermania.hungergames.perks;

import net.hungermania.hungergames.user.GameUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemPerk extends FlatPerk {
    
    private ItemStack itemStack;
    
    public ItemPerk(String name, int baseCost, int chance, ItemStack itemStack, PerkCategory category) {
        super(name, baseCost, chance, itemStack.getType(), category);
        this.itemStack = itemStack;
    }
    
    public boolean activate(GameUser user) {
        if (super.activate(user)) {
            Player player = Bukkit.getPlayer(user.getName());
            player.getInventory().addItem(itemStack);
            return true;
        }
        
        return false;
    }
}
