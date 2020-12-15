package net.hungermania.maniacore.spigot.user;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class SpigotUser extends User {
    
    private PlayerBoard scoreboard;
    
    public SpigotUser(UUID uniqueId) {
        super(uniqueId);
    }
    
    public SpigotUser(UUID uniqueId, String name) {
        super(uniqueId, name);
    }
    
    public SpigotUser(Map<String, String> jedisData) {
        super(jedisData);
    }
    
    public SpigotUser(User user) {
        this(user.getId(), user.getUniqueId(), user.getName(), user.getNetworkExperience(), user.getCoins(), user.getRank(), user.getChannel(), user.getOnlineTime());
    }
    
    public SpigotUser(int id, UUID uniqueId, String name, long networkExperience, int coins, Rank rank, Channel channel, long onlineTime) {
        super(id, uniqueId, name, networkExperience, coins, rank, channel, onlineTime);
    }
    
    public void sendMessage(BaseComponent baseComponent) {
        getBukkitPlayer().spigot().sendMessage(baseComponent);
    }
    
    public void sendMessage(String s) {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.sendMessage(Utils.color(s));
        }
    }
    
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }
    
    public boolean hasPermission(String permission) {
        Player player = getBukkitPlayer();
        if (player != null) {
            if (permission == null || permission.equals("")) {
                return true;
            }
            
            return player.hasPermission(permission);
        }
        return false;
    }
    
    public boolean isOnline() {
        return getBukkitPlayer() != null;
    }
}
