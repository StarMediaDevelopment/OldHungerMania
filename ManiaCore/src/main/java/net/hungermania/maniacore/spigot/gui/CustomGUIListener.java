package net.hungermania.maniacore.spigot.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Provides additional functionality to a PaginatedGUI
 */
public interface CustomGUIListener {

    void onInventoryClick(InventoryClickEvent e);
}