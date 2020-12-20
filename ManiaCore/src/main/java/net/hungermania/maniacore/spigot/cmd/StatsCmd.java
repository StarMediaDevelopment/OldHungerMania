package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.leveling.Level;
import net.hungermania.maniacore.api.stats.Stats;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.manialib.util.Constants;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class StatsCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        User target;
        if (args.length > 0) {
            target = ManiaCore.getInstance().getUserManager().getUser(args[0]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.color("&cYou must provide a target."));
                return true;
            }
            
            target = ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
        }
        
        if (target == null) {
            sender.sendMessage(Utils.color("&cCould not determine the target of the command."));
            return true;
        }
    
        Level level = ManiaCore.getInstance().getLevelManager().getLevel(target.getStat(Stats.EXPERIENCE).getValueAsInt());
        Level nextLevel = ManiaCore.getInstance().getLevelManager().getLevels().getOrDefault(level.getNumber() + 1, ManiaCore.getInstance().getLevelManager().getLevel(0));
    
        User user = ManiaCore.getInstance().getUserManager().getUser(target.getUniqueId());
        int kills = user.getStat(Stats.HG_KILLS).getValueAsInt();
        int deaths = user.getStat(Stats.HG_DEATHS).getValueAsInt();
        double kdr = kills / (deaths * 1.0);
        
        int wins = user.getStat(Stats.HG_WINS).getValueAsInt();
        int losses = user.getStat(Stats.HG_GAMES).getValueAsInt() - wins;
        double wlr = wins / (losses * 1.0);
        
        int deathmatches = user.getStat(Stats.HG_DEATHMATCHES).getValueAsInt();
        int chestsFound = user.getStat(Stats.HG_CHESTS_FOUND).getValueAsInt();
    
        sender.sendMessage(Utils.color("&6&l>> &a" + target.getName() + "'s Stats"));
        sender.sendMessage(Utils.color("&6&l> &7Coins: &b" + target.getStat(Stats.COINS).getValueAsInt()));
        sender.sendMessage(Utils.color("&6&l> &7Level: &b" + level.getNumber()));
        sender.sendMessage(Utils.color("&6&l> &7Experience: &b" + target.getStat(Stats.EXPERIENCE).getValueAsInt() + "     &e&lNext Level: &b" + (nextLevel.getTotalXp() - target.getStat(Stats.EXPERIENCE).getValueAsInt())));
        sender.sendMessage(Utils.color("&6&l> &7Kills: &b" + kills));
        sender.sendMessage(Utils.color("&6&l> &7Deaths: &b" + deaths));
        sender.sendMessage(Utils.color("&6&l> &7K/D: &b" + Constants.NUMBER_FORMAT.format(kdr)));
        sender.sendMessage(Utils.color("&6&l> &7Wins: &b" + wins));
        sender.sendMessage(Utils.color("&6&l> &7Losses: &b" + losses));
        sender.sendMessage(Utils.color("&6&l> &7W/L: &b" + Constants.NUMBER_FORMAT.format(wlr)));
        sender.sendMessage(Utils.color("&6&l> &7Win Streak: &b" + user.getStat(Stats.HG_WINSTREAK).getValue()));
        sender.sendMessage(Utils.color("&6&l> &7Deathmatches Reached: &b" + deathmatches));
        sender.sendMessage(Utils.color("&6&l> &7Chests Found: &b" + chestsFound));
        
        return true;
    }
}
