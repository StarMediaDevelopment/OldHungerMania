package net.hungermania.maniacore.spigot.perks.gui;

import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.perks.Perk;
import net.hungermania.maniacore.spigot.perks.Perks;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.Bukkit;

public class PerkMainGui extends Gui {
    public PerkMainGui(SpigotUser user) {
        super(Bukkit.getPluginManager().getPlugin("ManiaCorePlugin"), "Perks", false, 27);
    
        for (Perk perk : Perks.PERKS) {
            GUIButton button = new GUIButton(perk.getIcon(user));
            button.setListener(e -> { 
                perk.handlePurchase(user); 
                refreshInventory(e.getWhoClicked());
            });
            addButton(button);
        }
    }
}
