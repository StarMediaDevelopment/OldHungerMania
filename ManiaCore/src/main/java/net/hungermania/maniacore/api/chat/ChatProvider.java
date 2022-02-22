package net.hungermania.maniacore.api.chat;

import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.manialib.util.Priority;

public class ChatProvider {
    
    private ManiaPlugin maniaPlugin;
    private ChatHandler handler;
    private Priority priority;
    
    public ChatProvider(ManiaPlugin maniaPlugin, ChatHandler handler, Priority priority) {
        this.maniaPlugin = maniaPlugin;
        this.handler = handler;
        this.priority = priority;
    }
    
    public ManiaPlugin getManiaPlugin() {
        return maniaPlugin;
    }
    
    public ChatHandler getHandler() {
        return handler;
    }
    
    public Priority getPriority() {
        return priority;
    }
}
