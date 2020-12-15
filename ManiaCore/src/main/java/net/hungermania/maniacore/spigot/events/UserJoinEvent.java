package net.hungermania.maniacore.spigot.events;

import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserJoinEvent extends PlayerJoinEvent {
    
    private static final HandlerList handlers = new HandlerList();
    private SpigotUser spigotUser;
    
    public UserJoinEvent(SpigotUser spigotUser) {
        super(spigotUser.getBukkitPlayer(), null);
        this.spigotUser = spigotUser;
    }
    
    public SpigotUser getSpigotUser() {
        return spigotUser;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public String getJoinMessage() {
        return null;
    }
}
