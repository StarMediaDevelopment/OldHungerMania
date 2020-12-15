package net.hungermania.maniacore.api.ranks;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.redis.RedisListener;
import net.hungermania.maniacore.api.user.User;

import java.util.UUID;

public class RankRedisListener implements RedisListener {
    public void onCommand(String cmd, String[] args) {
        if (cmd.equalsIgnoreCase("rankUpdate")) {
            UUID uuid = UUID.fromString(args[0]);
            Rank rank = Rank.valueOf(args[1]);
            User user = ManiaCore.getInstance().getUserManager().getUser(uuid);
            if (user.getRank() != rank) {
                user.setRank(rank);
            }
        }
    }
}