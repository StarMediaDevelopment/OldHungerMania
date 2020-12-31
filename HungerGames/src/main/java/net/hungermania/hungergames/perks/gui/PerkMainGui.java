package net.hungermania.hungergames.perks.gui;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.perks.Perk;
import net.hungermania.hungergames.perks.Perks;
import net.hungermania.hungergames.user.GameUser;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;

public class PerkMainGui extends Gui {
    public PerkMainGui(GameUser user) {
        super(HungerGames.getInstance(), "Perks", false, 27);
    
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
