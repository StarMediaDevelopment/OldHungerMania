package net.hungermania.maniacore.spigot.gui;

import org.bukkit.inventory.ItemStack;

public class GUIButton {
    
    protected ButtonListener listener;
    protected ItemStack item;
    protected boolean allowRemoval = false;
    
    /**
     * Creates a GUIButton with the {@link ItemStack} as it's 'icon' in the inventory.
     *
     * @param item The desired 'icon' for the GUIButton.
     */
    public GUIButton(ItemStack item){
        this.item = item;
    }
    
    /**
     * @param listener The listener to be executed on button click.
     */
    public GUIButton setListener(ButtonListener listener){
        this.listener = listener;
        return this;
    }
    
    /**
     * @return The listener to be executed on button click.
     */
    public ButtonListener getListener(){
        return listener;
    }
    
    /**
     * Returns the {@link ItemStack} that will be used as the GUIButton's 'icon' in an inventory.
     *
     * @return The GUIButton's 'icon'.
     */
    public ItemStack getItem(){
        return item;
    }
    
    public void setItem(ItemStack itemStack) {
        this.item = itemStack;
    }
    
    public boolean getAllowRemoval() {
        return allowRemoval;
    }
    
    public void setAllowRemoval(boolean allowRemoval) {
        this.allowRemoval = allowRemoval;
    }
}