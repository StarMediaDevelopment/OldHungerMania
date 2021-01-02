package net.hungermania.maniacore.spigot.perks.cmd;

import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.perks.gui.PerkMainGui;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PerkCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ManiaUtils.color("&cOnly players may use that command."));
            return true;
        }
        SpigotUser user = (SpigotUser) ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
        new PerkMainGui(user).openGUI((Player) sender);
        return true;
    }
}
