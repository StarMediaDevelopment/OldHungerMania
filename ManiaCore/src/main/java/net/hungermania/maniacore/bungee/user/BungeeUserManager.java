package net.hungermania.maniacore.bungee.user;

import net.hungermania.maniacore.ManiaCoreProxy;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.user.UserManager;
import redis.clients.jedis.Jedis;

import java.util.*;

public class BungeeUserManager extends UserManager {
    
    public BungeeUserManager(ManiaCoreProxy plugin) {
        plugin.runTaskTimerAsynchronously(() -> {
            try (Jedis jedis = Redis.getConnection()) {
                Set<String> keys = jedis.keys("USER:*");
                for (String key : keys) {
                    UUID uuid = UUID.fromString(key.split(":")[1]);
                    BungeeUser bungeeUser = new BungeeUser(Redis.getUserData(uuid));
                    ManiaCoreProxy.saveUserData(bungeeUser);
                }
            }
        }, 20L, 6000L);
        
    }
    
    public User constructUser(UUID uuid, String name) {
        return new BungeeUser(uuid, name);
    }
    
    public User constructUser(Map<String, String> data) {
        return new BungeeUser(data);
    }
    
    public User constructUser(User user) {
        return new BungeeUser(user);
    }
}
