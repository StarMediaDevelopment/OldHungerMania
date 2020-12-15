package net.hungermania.maniacore.spigot.events;

import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.event.*;

import java.util.*;

public class UserIncognitoEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private String cancelledReason = "";
    
    private SpigotUser user;
    private boolean oldValue, newValue;
    private Map<UUID, Boolean> affectedPlayers;
    
    public UserIncognitoEvent(SpigotUser user, boolean oldValue, boolean newValue, Map<UUID, Boolean> affectedPlayers) {
        this.user = user;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.affectedPlayers = affectedPlayers;
    }
    
    public Map<UUID, Boolean> getAffectedPlayers() {
        return affectedPlayers;
    }
    
    public SpigotUser getUser() {
        return user;
    }
    
    public boolean oldValue() {
        return oldValue;
    }
    
    public boolean newValue() {
        return newValue;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
    
    public void setCancelled(boolean value, String reason) {
        this.cancelled = value;
        this.cancelledReason = reason;
    }
    
    public String getCancelledReason() {
        return cancelledReason;
    }
}
