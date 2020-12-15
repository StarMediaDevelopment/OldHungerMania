package net.hungermania.hungergames.user;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.maniacore.spigot.user.SpigotUserManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class HGUserManager extends SpigotUserManager {
    public HGUserManager(ManiaPlugin plugin) {
        super(plugin);
    }
    
    public User constructUser(UUID uuid, String name) {
        return new GameUser(uuid, name);
    }
    
    public User constructUser(Map<String, String> data) {
        return new GameUser(data);
    }
    
    public User constructUser(User user) {
        return new GameUser(user);
    }
    
    public User getUser(UUID uuid) {
        GameUser user = (GameUser) super.getUser(uuid);
        if (user.getPerks().isEmpty()) {
            new BukkitRunnable() {
                public void run() {
                    user.loadPerks();
                }
            }.runTaskLater(HungerGames.getInstance(), 1L);
        }
        return user;
    }
}
