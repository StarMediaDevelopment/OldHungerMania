package net.hungermania.hungergames.listeners;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.gui.SpectatorInventoryGui;
import net.hungermania.maniacore.spigot.gui.GUIButton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryListeners implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryInteractEvent e) {
        Player player = (Player) e.getWhoClicked();
        new BukkitRunnable() {
            public void run() {
                for (InventoryHolder guiInstance : SpectatorInventoryGui.getGuiInstances()) {
                    SpectatorInventoryGui gui = (SpectatorInventoryGui) guiInstance;
                    if (gui.getTarget().getUniqueId().equals(player.getUniqueId())) {
                        for (int i = SpectatorInventoryGui.OFFSET; i < gui.getItems().values().size(); i++) {
                            GUIButton button = gui.getButton(i);
                            button.setItem(e.getInventory().getItem(i - SpectatorInventoryGui.OFFSET));
                            gui.refreshInventory(gui.getPlayer().getUser().getBukkitPlayer());
                        }
                    }
                }
            }
        }.runTaskLater(HungerGames.getInstance(), 1L);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof SpectatorInventoryGui) {
            SpectatorInventoryGui.getGuiInstances().remove(e.getInventory().getHolder());
        }
    }
}
