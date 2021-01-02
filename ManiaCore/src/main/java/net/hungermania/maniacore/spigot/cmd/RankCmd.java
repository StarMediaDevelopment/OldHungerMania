package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.records.UserRecord;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class RankCmd implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank;
        if (sender instanceof Player) {
            User user = ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
            senderRank = user.getRank();
            if (!user.hasPermission(Rank.ADMIN)) {
                user.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
        } else {
            senderRank = Rank.CONSOLE;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(ManiaManiaUtils.color("&cUsage: /rank <name> [rank]"));
            return true;
        }
    
        User target = ManiaCore.getInstance().getUserManager().getUser(args[0]);
        if (target == null) {
            sender.sendMessage(ManiaManiaUtils.color("&cCould not find a user with that name."));
            return true;
        }
        
        if (args.length == 1) {
            sender.sendMessage(ManiaManiaUtils.color("&6&l>> &e" + target.getName() + "&f's rank is currently " + target.getRank().getBaseColor() + target.getRank().name()));
            return true;
        }
        
        Rank newRank;
        try {
            newRank = Rank.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ManiaManiaUtils.color("Invalid rank name."));
            return true;
        }
    
        if (newRank == Rank.CONSOLE || newRank == Rank.ROOT) {
            sender.sendMessage(ManiaManiaUtils.color("&cYou are not allowed to use those ranks as they cannot be set via commands."));
            return true;
        }
        
        if (target.getRank().ordinal() <= senderRank.ordinal()) {
            sender.sendMessage(ManiaManiaUtils.color("&cYou cannot modify that player's rank as their rank is equal to or higher than yours."));
            return true;
        }
        
        if (newRank.ordinal() <= senderRank.ordinal()) {
            sender.sendMessage(ManiaManiaUtils.color("&cYou are not allowed to set that rank as it is equal to or higher than yours."));
            return true;
        }
        
        if (target.getRank() == newRank) {
            sender.sendMessage(ManiaManiaUtils.color("&cThat player already has that rank."));
            return true;
        }
        
        target.setRank(newRank);
        ManiaCore.getInstance().getDatabase().pushRecord(new UserRecord(target));
        Redis.sendCommand("rankUpdate " + target.getUniqueId().toString() + " " + newRank.name());
        sender.sendMessage(ManiaManiaUtils.color("&aSuccessfully set &b" + target.getName() + "&a's rank to " + newRank.getPrefix()));
        sender.sendMessage(ManiaManiaUtils.color("  &7&oYou must also set their LuckPerms group."));
        return true;
    }
}