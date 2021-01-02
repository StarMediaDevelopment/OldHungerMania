package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.*;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class MsgCmd implements CommandExecutor {
    
    private Map<UUID, UUID> lastMessage = new HashMap<>();
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ManiaManiaUtils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = (Player) sender, target = null;
        int messageStart = 0;
        
        if (cmd.getName().equalsIgnoreCase("message")) {
            if (!(args.length > 1)) {
                sender.sendMessage(ManiaManiaUtils.color("&cUsage: /message <player> <message>"));
                return true;
            }
            
            messageStart = 1;
            target = Bukkit.getPlayer(args[0]);
        } else if (cmd.getName().equalsIgnoreCase("reply")) {
            if (!(args.length > 0)) {
                sender.sendMessage(ManiaManiaUtils.color("&cUsage: /reply <message>"));
                return true;
            }
            
            messageStart = 0;
            UUID lastMsg = lastMessage.get(player.getUniqueId());
            if (lastMsg != null) {
                target = Bukkit.getPlayer(lastMsg);
            }
        }
        
        if (target == null) {
            player.sendMessage(ManiaManiaUtils.color("&cCould not find the message target. Are they offline?"));
            return true;
        }
        
        StringBuilder message = new StringBuilder();
        for (int i = messageStart; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        String format = "&b&l>> {player1} &7&l-> {player2}&8: &f{message}";
        format = format.replace("{message}", message.toString());
        
        UserManager userManager = ManiaCore.getInstance().getUserManager();
        User senderUser = userManager.getUser(player.getUniqueId()), targetUser = userManager.getUser(target.getUniqueId());
        if (!targetUser.getToggle(Toggles.PRIVATE_MESSAGES).getAsBoolean()) {
            Rank senderRank = senderUser.getRank(), targetRank = targetUser.getRank();
            if (senderUser.hasPermission(Rank.HELPER)) {
                if (senderRank.ordinal() > targetRank.ordinal()) {
                    senderUser.sendMessage("&cThat player has private messages turned off.");
                    return true;
                }
            } else {
                senderUser.sendMessage("&cThat player has private messages turned off.");
                return true;
            }
        }
    
        for (IgnoreInfo ignoredPlayer : targetUser.getIgnoredPlayers()) {
            if (ignoredPlayer.getIgnored().equals(senderUser.getUniqueId())) {
                senderUser.sendMessage("&cThat player has you ignored, you cannot message them.");
                return true;
            }
        }
        
        senderUser.sendMessage(format.replace("{player1}", "&eme").replace("{player2}", targetUser.getDisplayName()));
        targetUser.sendMessage(format.replace("{player1}", senderUser.getDisplayName()).replace("{player2}", "&eme"));
        this.lastMessage.put(player.getUniqueId(), target.getUniqueId());
        this.lastMessage.put(target.getUniqueId(), player.getUniqueId());
        return true;
    }
}
