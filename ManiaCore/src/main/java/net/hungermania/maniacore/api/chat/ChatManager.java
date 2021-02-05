package net.hungermania.maniacore.api.chat;

import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.manialib.util.Priority;

import java.util.*;

public class ChatManager {
    
    private Set<ChatProvider> chatProviders = new HashSet<>();
    private Map<Channel, ChatFormatter> formatters = new HashMap<>();
    
    public static final ChatFormatter DEFAULT_FORMATTER = new ChatFormatter("{name}&8: {message}");
    
    public static final ChatHandler DEFAULT_HANDLER = new ChatHandler() {
        public Set<UUID> getAllTargets() {
            return new HashSet<>();
        }
    };
    
    public ChatFormatter getChatFormatter(Channel channel) {
        ChatFormatter chatFormatter = this.formatters.get(channel);
        return chatFormatter != null ? chatFormatter : DEFAULT_FORMATTER;
    }
    
    public void setFormatter(Channel channel, ChatFormatter formatter) {
        this.formatters.put(channel, formatter);
    }
    
    public void registerHandler(ManiaPlugin plugin, ChatHandler chatHandler, Priority priority) {
        this.chatProviders.add(new ChatProvider(plugin, chatHandler, priority));
    }
    
    public ChatHandler getHandler() {
        ChatProvider provider = null;
        for (ChatProvider chatProvider : chatProviders) {
            if (provider == null) {
                provider = chatProvider;
            } else {
                if (provider.getPriority().ordinal() < chatProvider.getPriority().ordinal()) {
                    provider = chatProvider;
                }
            }
        }
        return provider == null ? DEFAULT_HANDLER : provider.getHandler();
    }
}