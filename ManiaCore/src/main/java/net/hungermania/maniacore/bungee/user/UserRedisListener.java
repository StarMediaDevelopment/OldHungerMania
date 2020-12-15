package net.hungermania.maniacore.bungee.user;

import net.hungermania.maniacore.ManiaCoreProxy;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.redis.RedisListener;

import java.util.UUID;

public class UserRedisListener implements RedisListener {
    public void onCommand(String cmd, String[] args) {
        if (cmd.equalsIgnoreCase("saveUserData")) {
            UUID uuid = UUID.fromString(args[0]);
            BungeeUser bungeeUser = new BungeeUser(Redis.getUserData(uuid));
            ManiaCoreProxy.saveUserData(bungeeUser);
        }
    }
}
