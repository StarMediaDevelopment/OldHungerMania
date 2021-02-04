package net.hungermania.maniacore.api.chat;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class ChatHandler {

    public abstract Set<UUID> getAllTargets(); //Gets all possible chat targets, useful for the filtering methods 

    //Messages with no sender
    public void sendGlobalMessage(String message) {
        for (UUID target : getAllTargets()) {
            User user = ManiaCore.getInstance().getUserManager().getUser(target);
            if (user != null) {
                user.sendMessage(message);
            }
        }
    }
    
    //Will use the getMessageTargets method, which will provide ignore filtering and then process it that way using a ChatFormatter
    public void sendChatMessage(UUID sender, Channel channel, String message) {
        ChatFormatter chatFormatter = ManiaCore.getInstance().getChatManager().getChatFormatter(channel);
        String formattedMessage = chatFormatter.format(ManiaCore.getInstance().getUserManager().getUser(sender), message);
        Set<UUID> targets = getMessageTargets(sender);
        sendMessage(formattedMessage, targets);
    }
    
    //Sends a message with the target list
    public void sendMessage(String message, Set<UUID> targets) {
        for (UUID target : targets) {
            User user = ManiaCore.getInstance().getUserManager().getUser(target);
            if (user != null) {
                user.sendMessage(message);
            }
        }
    }
    
    //Sends a message that is to be filtered to a minimum rank
    public void sendTargetedMessage(Rank rank, String message) {
        for (UUID target : getAllTargets()) {
            User user = ManiaCore.getInstance().getUserManager().getUser(target);
            if (user != null) {
                if (user.hasPermission(rank)) {
                    user.sendMessage(message);
                }
            }
        }
    }
    
    //Sends a message that is to be filtered to a minimum rank with a sender
    public void sendTargetedMessage(Rank rank, UUID sender, String message) {
        for (UUID target : getMessageTargets(sender)) {
            User user = ManiaCore.getInstance().getUserManager().getUser(target);
            if (user != null) {
                if (user.hasPermission(rank)) {
                    user.sendMessage(message);
                }
            }
        }
    }
    
    //Filters targets based on a sender, this is global
    public Set<UUID> getMessageTargets(UUID sender) {
        Set<UUID> targets = new HashSet<>(getAllTargets());
        User user = ManiaCore.getInstance().getUserManager().getUser(sender);
        targets.removeIf(user::isIgnoring);
        return targets;
    }
}
