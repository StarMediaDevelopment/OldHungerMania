package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.friends.*;
import net.hungermania.maniacore.api.friends.FriendNotification.Type;
import net.hungermania.maniacore.api.pagination.*;
import net.hungermania.maniacore.api.records.FriendRequestRecord;
import net.hungermania.maniacore.api.records.FriendshipRecord;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.user.IgnoreInfo;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.manialib.util.Pair;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class FriendsCmd implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ManiaManiaUtils.color("&cOnly players may use that command."));
            return true;
        }
        
        User user = ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
        
        if (!(args.length > 0)) {
            user.sendMessage("&cUsage: /friends <subcommand>");
            return true;
        }
        
        FriendsManager friendsManager = ManiaCore.getInstance().getFriendsManager();
        
        if (ManiaUtils.checkCmdAliases(args, 0, "list", "l")) {
            List<Friendship> friendships = friendsManager.getFriendships(user.getUniqueId());
            Paginator<Friendship> paginator = PaginatorFactory.generatePaginator(7, friendships, new HashMap<DefaultVariables, String>() {{
                put(DefaultVariables.TYPE, "Friends");
                put(DefaultVariables.COMMAND, "/friends list");
            }});
            if (args.length == 1) {
                paginator.display(user, 1, user.getUniqueId().toString());
            } else {
                paginator.display(user, args[1], user.getUniqueId().toString());
            }
        } else if (ManiaUtils.checkCmdAliases(args, 0, "add")) {
            if (!(args.length > 1)) {
                user.sendMessage("&cUsage: /friends add <name>");
                return true;
            }
            
            User target = ManiaCore.getInstance().getUserManager().getUser(args[1]);
            if (target == null) {
                user.sendMessage("&cCould not find a user with that name.");
                return true;
            }
            
            String message = "";
            FriendResult result = friendsManager.addRequest(user, target);
            switch (result) {
                case ALREADY_FRIENDS:
                    message = "&cYou are already friends with " + target.getName();
                    break;
                case EXISTING_REQUEST:
                    message = "&cThere is already a pending friend request between you and " + target.getName();
                    break;
                case DATABASE_ERROR:
                    message = "&cThere was a database error when generating the friend request.";
                    break;
                case SUCCESS:
                    message = "&aSuccessfully sent a friend request to " + target.getName();
                    break;
            }
    
            for (IgnoreInfo ignoredPlayer : target.getIgnoredPlayers()) {
                if (ignoredPlayer.getIgnored().equals(user.getUniqueId())) {
                    user.sendMessage("&cThat player has you ignored, you cannot message them.");
                    return true;
                }
            }
            
            if (result == FriendResult.SUCCESS) {
                if (target.isOnline()) {
                    target.sendMessage("&aYou have received a friend request from " + user.getName());
                } else {
                    Redis.sendCommand("friendRequest " + user.getUniqueId().toString() + " " + target.getUniqueId());
                }
            }
            
            user.sendMessage(message);
        } else if (ManiaUtils.checkCmdAliases(args, 0, "remove")) {
            if (!(args.length > 1)) {
                user.sendMessage("&cUsage: /friends remove <name>");
                return true;
            }
            
            User target = ManiaCore.getInstance().getUserManager().getUser(args[1]);
            if (target == null) {
                user.sendMessage("&cCould not find a user with that name.");
                return true;
            }
            
            Pair<FriendResult, Friendship> resultPair = friendsManager.removeFriend(user, target);
            FriendResult result = resultPair.getValue1();
            String message = "";
            switch (result) {
                case NOT_FRIENDS:
                    message = "&cYou are not friends with " + target.getName();
                    break;
                case SUCCESS:
                    message = "&aSuccessfully removed " + target.getName() + " as a friend.";
                    break;
                default:
                    break;
            }
            
            if (result == FriendResult.SUCCESS) {
                if (!target.isOnline()) {
                    FriendNotification notification = new FriendNotification(Type.REMOVED, user.getUniqueId(), target.getUniqueId(), System.currentTimeMillis());
                    friendsManager.addNotification(notification);
                    Redis.sendCommand("friendNotification " + notification.getId());
                } else {
                    target.sendMessage("&a" + user.getName() + " has removed you as a friend.");
                }
                
                ManiaCore.getInstance().getDatabase().deleteRecord(new FriendshipRecord(resultPair.getValue2()));
            }
            
            user.sendMessage(message);
        } else if (ManiaUtils.checkCmdAliases(args, 0, "requests")) {
            //List
        } else if (ManiaUtils.checkCmdAliases(args, 0, "accept", "deny")) {
            if (!(args.length > 1)) {
                user.sendMessage("&cYou must provide a name.");
                return true;
            }
            
            User target = ManiaCore.getInstance().getUserManager().getUser(args[1]);
            if (target == null) {
                sender.sendMessage("&cYou provided an invalid name.");
                return true;
            }
            
            
            String message, targetMessage;
            Type notificationType;
            FriendResult result;
            Pair<FriendResult, FriendRequest> resultPair;
            if (ManiaUtils.checkCmdAliases(args, 0, "accept")) {
                resultPair = friendsManager.acceptRequest(user, target);
                result = resultPair.getValue1();
                switch (result) {
                    case ALREADY_FRIENDS:
                        message = "&cYou are already friends with " + target.getName();
                        break;
                    case NO_REQUEST:
                        message = "&cThere is no existing friend requests from " + target.getName();
                        break;
                    case SUCCESS:
                        message = "&aYou have accepted " + target.getName() + "'s friend request.";
                        break;
                    case REQUEST_SENDER:
                        message = "&cYou are the request sender so you cannot accept your own request.";
                        break;
                    case DATABASE_ERROR:
                        message = "&cThere was a database error processing your friendship.";
                        break;
                    default:
                        message = "";
                        break;
                }
                
                notificationType = Type.ACCEPTED;
                targetMessage = "&aYou and " + target.getName() + " are now friends.";
            } else {
                resultPair = friendsManager.denyRequest(user, target);
                result = resultPair.getValue1();
                switch (result) {
                    case ALREADY_FRIENDS:
                        message = "&cYou are friends with " + target.getName() + ", please use /friends remove <name> to remove them as a friend.";
                        break;
                    case NO_REQUEST:
                        message = "&cThere is no existing friend requests from " + target.getName();
                        break;
                    case SUCCESS:
                        message = "&aYou have denied " + target.getName() + "'s friend request.";
                        break;
                    case REQUEST_SENDER:
                        message = "&cYou are the request sender so you cannot deny your own request.";
                        break;
                    default:
                        message = "";
                        break;
                }
                
                notificationType = Type.DENIED;
                targetMessage = "&a" + user.getName() + " has denied your friend request.";
            }
            
            if (result == FriendResult.SUCCESS) {
                if (!target.isOnline()) {
                    FriendNotification notification = new FriendNotification(notificationType, user.getUniqueId(), target.getUniqueId(), System.currentTimeMillis());
                    friendsManager.addNotification(notification);
                    Redis.sendCommand("friendNotification " + notification.getId());
                } else {
                    target.sendMessage(targetMessage);
                }
                ManiaCore.getInstance().getDatabase().deleteRecord(new FriendRequestRecord(resultPair.getValue2()));
            }
            
            user.sendMessage(message);
        }
        
        return true;
    }
}
