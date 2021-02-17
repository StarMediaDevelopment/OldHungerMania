package net.hungermania.maniacore.spigot.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserActionBarUpdateEvent extends Event implements Cancellable {
    private static HandlerList handlerList = new HandlerList();
    
    private boolean cancelled;

    public UserActionBarUpdateEvent() {
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
