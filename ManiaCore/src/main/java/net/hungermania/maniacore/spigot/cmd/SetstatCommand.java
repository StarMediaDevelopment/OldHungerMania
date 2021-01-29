package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetstatCommand implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank rank;
        if (sender instanceof ConsoleCommandSender) {
            rank = Rank.CONSOLE;
        } else if (sender instanceof Player) {
            User user = ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
            rank = user.getRank();
        } else {
            sender.sendMessage(ManiaUtils.color("&cYou are not allowed to use that command."));
            return true;
        }
        
        if (rank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(ManiaUtils.color("&cYou do not have permission to use that command."));
            return true;
        }
        
        if (!(args.length > 2)) {
            sender.sendMessage(ManiaUtils.color("&cUsage: /setstat <player> <statname> <value>"));
            return true;
        }
        
        User target = ManiaCore.getInstance().getUserManager().getUser(args[0]);
        if (target == null) {
            sender.sendMessage(ManiaUtils.color("&cThe name you provided does not match a player that has joined the server."));
            return true;
        }
        
        Stats stat;
        try {
            stat = Stats.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ManiaUtils.color("&cInvalid stat name."));
            return true;
        }
        
        Statistic statistic = target.getStat(stat);
        if (!stat.isNumber()) {
            statistic.setValue(StringUtils.join(args, " ", 2, args.length));
            sender.sendMessage(ManiaUtils.color("&aSet the stat " + stat.name().toLowerCase() + " to " + statistic.getValue()));
        } else {
            String a = args[2];
            if (a.startsWith("+")) {
                int v = Integer.parseInt(a.substring(1));
                statistic.setValue((statistic.getAsInt() + v) + "");
                sender.sendMessage(ManiaUtils.color("&aIncreased the stat " + stat.name().toLowerCase() + " by " + v));
            } else if (a.startsWith("-")) {
                int v = Integer.parseInt(a.substring(1));
                statistic.setValue((statistic.getAsInt() - v) + "");
                sender.sendMessage(ManiaUtils.color("&aDecreased the stat " + stat.name().toLowerCase() + " by " + v));
            } else {
                int v = Integer.parseInt(a);
                statistic.setValue(v + "");
                sender.sendMessage(ManiaUtils.color("&aSet the stat " + stat.name().toLowerCase() + " to " + v));
            }
        }
        
        return true;
    }
}
