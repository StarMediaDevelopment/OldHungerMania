package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("hungermania.command.say")) {
            String senderName;
            if (sender instanceof Player) {
                User user = ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
                senderName = user.getDisplayName();
            } else {
                senderName = sender.getName();
            }
            String message = "&8[&f&l&oSAY&8] &b" + senderName + ": &b" + StringUtils.join(args, " ");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(Utils.color(message));
            }
            Bukkit.getConsoleSender().sendMessage(Utils.color(message));
        }
        return true;
    }
}