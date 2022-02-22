package net.hungermania.maniacore.spigot.events;

import net.hungermania.maniacore.api.events.EventInfo;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ManiaEventStatusChange extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private EventInfo eventInfo;
    
    public ManiaEventStatusChange(EventInfo eventInfo) {
        super();
        this.eventInfo = eventInfo;
    }
    
    public EventInfo getEventInfo() {
        return eventInfo;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
}