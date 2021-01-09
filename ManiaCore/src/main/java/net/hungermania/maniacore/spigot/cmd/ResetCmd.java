package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.reset.ResetAction;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ResetCmd implements CommandExecutor {
    
    private Map<String, ResetAction> resetConfirmation = new HashMap<>();
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank;
        if (sender instanceof ConsoleCommandSender) {
            senderRank = Rank.CONSOLE;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
            senderRank = user.getRank();
        } else {
            senderRank = Rank.DEFAULT;
        }
    
        // Stats, Toggles, Perks, Mutations, User (Owner), Friends, Games (Owner)
        // All (no args)
    
        if (cmd.getName().equals("reset")) {
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                sender.sendMessage(ManiaUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
        
        
        } else if (cmd.getName().equals("resetall")) {
            if (senderRank.ordinal() > Rank.OWNER.ordinal()) {
                sender.sendMessage(ManiaUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
        } else if (cmd.getName().equals("resetconfirm")) {
            
        } else if (cmd.getName().equals("resetallconfirm")) {
            
        }
        
        
        return true;
    }
}
