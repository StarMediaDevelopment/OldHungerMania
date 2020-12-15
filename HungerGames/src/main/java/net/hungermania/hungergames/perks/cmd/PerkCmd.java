package net.hungermania.hungergames.perks.cmd;

import net.hungermania.hungergames.perks.gui.PerkGui;
import net.hungermania.hungergames.user.GameUser;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.util.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PerkCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        GameUser user = (GameUser) ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
        new PerkGui(user).openGUI((Player) sender);
        return true;
    }
}
