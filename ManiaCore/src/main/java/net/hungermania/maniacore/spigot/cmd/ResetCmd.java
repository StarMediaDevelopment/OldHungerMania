package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.spigot.reset.ResetAction;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ResetCmd implements CommandExecutor {
    
    private Map<String, ResetAction> resetConfirmation = new HashMap<>();
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = null;
        if (sender instanceof ConsoleCommandSender) {
            senderRank = Rank.CONSOLE;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
            senderRank = user.getRank();
        } else {
            senderRank = Rank.DEFAULT;
        }
        
        if (cmd.getName().equals("reset")) {
            
        } else if (cmd.getName().equals("resetall")) {
            
        } else if (cmd.getName().equals("resetconfirm")) {
            
        } else if (cmd.getName().equals("resetallconfirm")) {
            
        }
        
        
        return true;
    }
}
