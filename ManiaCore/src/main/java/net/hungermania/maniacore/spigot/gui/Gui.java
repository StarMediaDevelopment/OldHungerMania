package net.hungermania.maniacore.spigot.gui;

import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Gui implements InventoryHolder {
    
    private static InventoryListenerGUI inventoryListenerGUI;
    protected List<CustomGUIListener> extraListeners;
    protected Map<Integer, GUIButton> items;
    protected Map<Integer, GUIButton> toolbarItems;
    protected int currentPage, maxSlots;
    protected String name;
    protected Plugin plugin;
    protected boolean paginated;
    protected boolean allowInsert;
    protected List<Integer> allowedInsertSlots = new ArrayList<>();
    
    public Gui(Plugin plugin, String name) {
        this(plugin, name, true);
    }
    
    public Gui(Plugin plugin, String name, boolean paginated) {
        this(plugin, name, paginated, 54);
    }
    
    public Gui(Plugin plugin, String name, boolean paginated, int maxSlots) {
        this(plugin, name, paginated, maxSlots, false);
    }
    
    public Gui(Plugin plugin, String name, boolean paginated, int maxSlots, boolean allowInsert) {
        items = new HashMap<>();
        toolbarItems = new HashMap<>();
        currentPage = 0;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.plugin = plugin;
        this.paginated = paginated;
        this.maxSlots = maxSlots;
        this.allowInsert = allowInsert;
        this.extraListeners = new ArrayList<>();
    }
    
    public boolean getAllowInsert() {
        return allowInsert;
    }
    
    public void setDisplayName(String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
    }
    
    public String getDisplayName() {
        return name;
    }
    
    public int addButton(GUIButton button) {
        int slot = 0;
        
        if (!items.isEmpty()) {
            // Find the highest slot
            int highestSlot = -1;
            for (int itemSlot : items.keySet()) {
                if (itemSlot > highestSlot) {
                    highestSlot = itemSlot;
                }
            }
            
            // Set the target slot to one higher than the highest slot.
            slot = highestSlot + 1;
        }
        
        // Put the button in that slot.
        items.put(slot, button);
        return slot;
    }
    
    public void setButton(int slot, GUIButton button) {
        items.put(slot, button);
    }
    
    public void removeButton(int slot) {
        items.remove(slot);
    }
    
    public GUIButton getButton(int slot) {
        if (slot < 45) {
            int page = currentPage;
            int index = slot;
            if (paginated) {
                if (page > 0) index = (page * 45) + slot;
            }
            
            return items.get(index);
        }
        return toolbarItems.get(slot - 45);
    }
    
    public void setToolbarItem(int slot, GUIButton button) {
        if (slot < 0 || slot > 8) {
            throw new IllegalArgumentException("The desired slot is outside the bounds of the toolbar slot range. [0-8]");
        }
        
        toolbarItems.put(slot, button);
    }
    
    public void removeToolbarItem(int slot) {
        if (slot < 0 || slot > 8) {
            throw new IllegalArgumentException("The desired slot is outside the bounds of the toolbar slot range. [0-8]");
        }
        
        toolbarItems.remove(slot);
    }
    
    public boolean nextPage() {
        if (currentPage < getFinalPage()) {
            currentPage++;
            return true;
        }
        return false;
    }
    
    public boolean previousPage() {
        if (currentPage > 0) {
            currentPage--;
            return true;
        }
        return false;
    }
    
    public int getMaxPage() {
        return getFinalPage();
    }
    
    public int getFinalPage() {
        // Get the highest slot number.
        int slot = 0;
        for (int nextSlot : items.keySet()) {
            if (nextSlot > slot) {
                slot = nextSlot;
            }
        }
        
        // Add one to make the math easier.
        double highestSlot = slot + 1;
        
        // Divide by 45 and round up to get the page number.
        // Then subtract one to convert it to an index.
        return (int) Math.ceil(highestSlot / (double) 45) - 1;
    }
    
    public void refreshInventory(HumanEntity holder) {
        new BukkitRunnable() {
            public void run() {
                holder.openInventory(getInventory());
            }
        }.runTaskLater(plugin, 1L);
    }
    
    @Override
    public Inventory getInventory() {
        // Create an inventory (and set an appropriate size.)
        Inventory inventory;
        
        if (paginated) {
            inventory = Bukkit.createInventory(this, 54, name);
            GUIButton backButton = new GUIButton(new ItemBuilder(Material.ARROW).setDisplayName("&aPrevious Page").build());
            GUIButton pageIndicator = new GUIButton(new ItemBuilder(Material.NAME_TAG).setDisplayName("&aPage " + (currentPage + 1) + " of " + (this.getFinalPage() + 1)).build());
            GUIButton nextButton = new GUIButton(new ItemBuilder(Material.ARROW).setDisplayName("&aNext Page").build());
            
            backButton.setListener(event -> {
                event.setCancelled(true);
                Gui menu = (Gui) event.getInventory().getHolder();
                
                if (!menu.previousPage()) {
                    event.getWhoClicked().sendMessage(ManiaUtils.color("&cThere are no previous pages"));
                    return;
                }
                
                refreshInventory(event.getWhoClicked());
            });
            
            pageIndicator.setListener(event -> event.setCancelled(true));
            
            nextButton.setListener(event -> {
                event.setCancelled(true);
                Gui menu = (Gui) event.getInventory().getHolder();
                
                if (!menu.nextPage()) {
                    event.getWhoClicked().sendMessage(ManiaUtils.color("&cThere are no additional pages"));
                    return;
                }
                
                refreshInventory(event.getWhoClicked());
            });
            /* END PAGINATION */

//            // Where appropriate, include pagination.
//            if (currentPage > 0) toolbarItems.put(3, backButton);
//            if (getFinalPage() > 0) toolbarItems.put(4, pageIndicator);
//            if (currentPage < getFinalPage()) toolbarItems.put(5, nextButton);
            
            toolbarItems.put(3, backButton);
            toolbarItems.put(4, pageIndicator);
            toolbarItems.put(5, nextButton);
            
        } else {
            inventory = Bukkit.createInventory(this, maxSlots, name);
        }
        
        // Add the main inventory items
        int counter = 0;
        int maxItems = (toolbarItems.isEmpty()) ? maxSlots : 45;
        
        if (!items.isEmpty()) {
            for (int key = (currentPage * maxItems); key <= Collections.max(items.keySet()); key++) {
                if (counter >= maxItems) {
                    break;
                }
                
                if (items.containsKey(key)) {
                    inventory.setItem(counter, items.get(key).getItem());
                }
                
                counter++;
            }
        }
        
        if (!toolbarItems.isEmpty()) {
            for (int toolbarItem : toolbarItems.keySet()) {
                int rawSlot = toolbarItem + 45;
                inventory.setItem(rawSlot, toolbarItems.get(toolbarItem).getItem());
            }
        }
        
        return inventory;
    }
    
    public static void prepare(JavaPlugin plugin) {
        if (inventoryListenerGUI == null) {
            inventoryListenerGUI = new InventoryListenerGUI();
            plugin.getServer().getPluginManager().registerEvents(inventoryListenerGUI, plugin);
        }
    }
    
    public void openGUI(HumanEntity player) {
        player.openInventory(getInventory());
    }
    
    public void callExtraListeners(InventoryClickEvent event) {
        if (!this.extraListeners.isEmpty()) this.extraListeners.forEach(listener -> listener.onInventoryClick(event));
    }
    
    public void addExtraListener(CustomGUIListener listener) {
        this.extraListeners.add(listener);
    }
    
    public boolean allowedToInsert(int slot) {
        if (this.allowedInsertSlots.isEmpty()) {
            return true;
        }
        return this.allowedInsertSlots.contains(slot);
    }
    
    public void setAllowedToInsert(int... slots) {
        for (int slot : slots) {
            this.allowedInsertSlots.add(slot);
        }
    }
}