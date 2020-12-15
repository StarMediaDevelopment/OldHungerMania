package net.hungermania.maniacore.spigot.user;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.friends.FriendNotification;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.redis.RedisListener;
import net.hungermania.maniacore.api.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FriendsRedisListener implements RedisListener {
    public void onCommand(String cmd, String[] args) {
        if (Bukkit.getOnlinePlayers().size() == 0) return;
        if (cmd.equalsIgnoreCase("friendRequest")) {
            UUID sender = UUID.fromString(args[0]);
            UUID target = UUID.fromString(args[1]);
            User senderUser = ManiaCore.getInstance().getUserManager().getUser(sender);
            User targetUser = ManiaCore.getInstance().getUserManager().getUser(target);
            if (targetUser.isOnline() || !senderUser.isOnline()) {
                targetUser.sendMessage("&aYou have received a friend request from " + senderUser.getName());
            }
        } else if (cmd.equalsIgnoreCase("friendNotification")) {
            int id = Integer.parseInt(args[0]);
            FriendNotification notification = Redis.getFriendNotification(id);
            if (notification == null) return;
            
            User actor = ManiaCore.getInstance().getUserManager().getUser(notification.getSender());
            User target = ManiaCore.getInstance().getUserManager().getUser(notification.getTarget());
            
            if (target.isOnline() && !actor.isOnline()) {
                String message = "";
                switch (notification.getType()) {
                    case ACCEPTED: message = "&a" + actor.getName() + " has accepted your friend request.";
                        break;
                    case DENIED: message = "&a" + actor.getName() + " has denied your friend request.";
                        break;
                    case REMOVED: message = "&a" + actor.getName() + " has removed you as a friend.";
                        break;
                }
                target.sendMessage(message);
            }
        } else if (cmd.equalsIgnoreCase("userJoin")) {
            UUID uuid = UUID.fromString(args[0]);
            User user = ManiaCore.getInstance().getUserManager().getUser(uuid);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                User onlineUser = ManiaCore.getInstance().getUserManager().getUser(onlinePlayer.getUniqueId());
                if (ManiaCore.getInstance().getFriendsManager().getFriendship(uuid, onlineUser.getUniqueId()) != null) {
                    onlineUser.sendMessage("&eYour friend " + user.getName() + " joined.");
                }
            }
        }
    }
}
