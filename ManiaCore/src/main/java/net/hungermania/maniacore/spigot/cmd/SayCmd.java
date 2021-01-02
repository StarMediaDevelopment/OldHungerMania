package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
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
                player.sendMessage(ManiaUtils.color(message));
            }
            Bukkit.getConsoleSender().sendMessage(ManiaUtils.color(message));
        }
        return true;
    }
}