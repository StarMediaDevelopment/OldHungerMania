package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.spigot.mutations.MutateGui;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class MutationsCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            new MutateGui(((Player) sender).getUniqueId()).openGUI(((Player) sender));
        }
        return true;
    }
}