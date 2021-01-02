package net.hungermania.maniacore.spigot.perks;

import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemPerk extends FlatPerk {
    
    private ItemStack itemStack;
    
    public ItemPerk(String name, int baseCost, int chance, ItemStack itemStack, PerkCategory category, String description) {
        super(name, baseCost, chance, itemStack.getType(), category, description);
        this.itemStack = itemStack;
    }
    
    public boolean activate(SpigotUser user) {
        if (super.activate(user)) {
            Player player = Bukkit.getPlayer(user.getName());
            player.getInventory().addItem(itemStack);
            return true;
        }
        
        return false;
    }
}
