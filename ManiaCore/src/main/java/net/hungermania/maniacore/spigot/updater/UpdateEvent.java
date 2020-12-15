package net.hungermania.maniacore.spigot.updater;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();

	private UpdateType type;
	private long lastRun;

	public UpdateEvent(UpdateType type, long lastRun){
		this.type = type;
		this.lastRun = lastRun;
	}

	public UpdateType getType(){
		return type;
	}

	public long getLastRun(){
		return lastRun;
	}

	public static HandlerList getHandlerList(){
		return handlerList;
	}

	@Override
	public HandlerList getHandlers(){
		return handlerList;
	}
}
