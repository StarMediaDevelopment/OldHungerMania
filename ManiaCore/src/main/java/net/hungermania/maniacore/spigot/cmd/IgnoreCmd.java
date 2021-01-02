package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.user.*;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.manialib.util.Constants;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Date;

@SuppressWarnings("DuplicatedCode")
public class IgnoreCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ManiaUtils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!(args.length > 0)) {
            sender.sendMessage(ManiaUtils.color("&cUsage: /ignore <add|remove|list> [player]"));
            return true;
        }
        
        User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
        
        if (ManiaUtils.checkCmdAliases(args, 0, "add", "a")) {
            if (!(args.length > 1)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a player name."));
                return true;
            }
            
            User target = ManiaCore.getInstance().getUserManager().getUser(args[1]);
            if (target == null) {
                sender.sendMessage(ManiaUtils.color("&cYou provided an invalid name."));
                return true;
            }
            
            IgnoreResult result = user.addIgnoredPlayer(target);
            String message = "";
            switch (result) {
                case PLAYER_IS_STAFF:
                    message = "&cThat player is a staff member. You cannot ignore them.";
                    break;
                case SUCCESS:
                    message = "&aYou added " + target.getName() + " to your ignored players list";
                    break;
                case NOT_IGNORED:
                case DATABASE_ERROR:
                    message = "";
                    break;
                case ALREADY_ADDED:
                    message = "&cYou already have " + target.getName() + " on your ignored players list.";
                    break;
            }
            
            player.sendMessage(ManiaUtils.color(message));
        } else if (ManiaUtils.checkCmdAliases(args, 0, "remove", "r")) {
            if (!(args.length > 1)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a player name."));
                return true;
            }
            
            User target = ManiaCore.getInstance().getUserManager().getUser(args[1]);
            if (target == null) {
                sender.sendMessage(ManiaUtils.color("&cYou provided an invalid name."));
                return true;
            }
            
            IgnoreResult result = user.removeIgnoredPlayer(target);
            String message = "";
            switch (result) {
                case PLAYER_IS_STAFF:
                    message = "";
                    break;
                case NOT_IGNORED:
                    message = "&cThat player is not ignored.";
                    break;
                case DATABASE_ERROR:
                    message = "&cThere was an error removing that player from your ignored list.";
                    break;
                case SUCCESS:
                    message = "&aYou removed " + target.getName() + " from your ignored players list";
                    break;
            }
            
            player.sendMessage(ManiaUtils.color(message));
        } else if (ManiaUtils.checkCmdAliases(args, 0, "list", "l")) {
            player.sendMessage(ManiaUtils.color("&6&l>> &bAll ignored players."));
            for (IgnoreInfo ignoredPlayer : user.getIgnoredPlayers()) {
                player.sendMessage(ManiaUtils.color("&6&l> &b" + ignoredPlayer.getIgnoredName() + " &fwas ignored on &a" + Constants.DATE_FORMAT.format(new Date(ignoredPlayer.getTimestamp()))));
            }
        }
        
        return true;
    }
}
