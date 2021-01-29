package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TesterCmd implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank;
        if (sender instanceof ConsoleCommandSender) {
            senderRank = Rank.CONSOLE;
        } else if (sender instanceof Player) {
            senderRank = ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId()).getRank();
        } else {
            senderRank = Rank.DEFAULT;
        }

        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(ManiaUtils.color("&cYou do not have enough permission to use that command."));
            return true;
        }

        if (!(args.length > 1)) {
            sender.sendMessage(ManiaUtils.color("&cUsage: /tester <name> <true|false>"));
            return true;
        }

        User target = ManiaCore.getInstance().getUserManager().getUser(args[0]);
        if (target == null) {
            sender.sendMessage(ManiaUtils.color("&cInvalid target name."));
            return true;
        }

        Statistic statistic = target.getStat(Stats.TESTER);
        boolean current = statistic.getAsBoolean();
        boolean value;
        try {
            value = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            sender.sendMessage(ManiaUtils.color("&cYou provided an invalid value. Possible values: true or false"));
            return true;
        }

        if (value == current) {
            sender.sendMessage(ManiaUtils.color("&cThe new value is the same as the old value."));
            return true;
        }

        statistic.setValue(value);
        sender.sendMessage(ManiaUtils.color("&aYou set " + target.getName() + "'s tester status to " + value));
        return true;
    }
}
