package net.hungermania.maniacore.spigot.perks.gui;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import net.hungermania.maniacore.spigot.gui.Gui;
import net.hungermania.maniacore.spigot.perks.*;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

public class PerkMainGui extends Gui {
    public PerkMainGui(SpigotUser user) {
        super(Bukkit.getPluginManager().getPlugin("ManiaCorePlugin"), "Perks", false, 27);
    
        for (Perk perk : Perks.PERKS) {
            GUIButton button = new GUIButton(perk.getIcon(user));
            button.setListener(e -> {
                if (e.getClick() == ClickType.LEFT) {
                    perk.handlePurchase(user);
                } else if (e.getClick() == ClickType.RIGHT) {
                    PerkInfo perkInfo = ((SpigotUser) ManiaCore.getInstance().getUserManager().getUser(e.getWhoClicked().getUniqueId())).getPerkInfo(perk);
                    perkInfo.setActive(true);
                    new PerkInfoRecord(perkInfo).push(ManiaCore.getInstance().getDatabase());
                }
                refreshInventory(e.getWhoClicked());
            });
            addButton(button);
        }
    }
}
