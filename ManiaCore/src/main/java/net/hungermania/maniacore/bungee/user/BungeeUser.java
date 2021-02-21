package net.hungermania.maniacore.bungee.user;

import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.ranks.RankInfo;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.UUID;

public class BungeeUser extends User {
    public BungeeUser(UUID uniqueId) {
        super(uniqueId);
    }
    
    public BungeeUser(UUID uniqueId, String name) {
        super(uniqueId, name);
    }
    
    public BungeeUser(User user) {
        this(user.getId(), user.getUniqueId(), user.getName(), user.getRankInfo(), user.getChannel());
    }
    
    public BungeeUser(Map<String, String> jedisData) {
        super(jedisData);
    }
    
    public BungeeUser(int id, UUID uniqueId, String name, RankInfo rank, Channel channel) {
        super(id, uniqueId, name, rank, channel);
    }
    
    public void sendMessage(BaseComponent baseComponent) {
        getProxyPlayer().sendMessage(ChatMessageType.CHAT, baseComponent);
    }
    
    public void sendMessage(String s) {
        ProxiedPlayer proxiedPlayer = getProxyPlayer();
        if (proxiedPlayer != null) {
            proxiedPlayer.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(ManiaUtils.color(s)));
        }
    }
    
    public ProxiedPlayer getProxyPlayer() {
        return ProxyServer.getInstance().getPlayer(this.uniqueId);
    }
    
    public boolean hasPermission(String permission) {
        ProxiedPlayer player = getProxyPlayer();
        if (player != null) {
            return player.hasPermission(permission);
        }
        return false;
    }
    
    public boolean isOnline() {
        return getProxyPlayer() != null;
    }
}
