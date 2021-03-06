package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.ToggleRecord;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.user.toggle.Toggle;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ManiaUtils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = ((Player) sender);
        
        if (!(args.length > 0)) {
            player.sendMessage(ManiaUtils.color("&cYou must provide a type."));
            return true;
        }
    
        User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
    
        if (ManiaUtils.checkCmdAliases(args, 0, "list")) {
            user.sendMessage("&aList of toggles");
            for (Toggles toggle : Toggles.values()) {
                if (user.hasPermission(toggle.getRank())) {
                    user.sendMessage("&e" + toggle.name().toLowerCase());
                }
            }
            return true;
        }
        
        Toggles type = null;
        Toggle toggle;
        for (Toggles value : Toggles.values()) {
            if (value.getCmdName() != null) {
                if (value.getCmdName().equalsIgnoreCase(args[0])) {
                    type = value;
                }
            }
        }
        
        if (type == null) {
            user.sendMessage("&cCould not find a toggle with that name.");
            return true;
        }
        
        toggle = user.getToggle(type);
        if (!user.hasPermission(type.getRank())) {
            user.sendMessage(ManiaUtils.color("&cYou do not have permission to use that toggle."));
            return true;
        }
        
        toggle.setValue((!toggle.getAsBoolean()) + "");
        ManiaCore.getInstance().getDatabase().pushRecord(new ToggleRecord(toggle));
    
        String settingValue;
        if (toggle.getAsBoolean()) {
            settingValue = "&a&lON";
        } else {
            settingValue = "&c&lOFF";
        }
    
        player.sendMessage(ManiaUtils.color("&6&l>> &fYou have turned " + settingValue + " &fthe toggle &e" + type.name().toLowerCase().replace("_", " ") + "&f."));
        return true;
    }
}
