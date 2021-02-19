package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.ManiaCorePlugin;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.util.Spawnpoint;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCmd implements CommandExecutor {
    
    private ManiaCorePlugin plugin;

    public SpawnCmd(ManiaCorePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ManiaUtils.color("&cOnly players may use that command."));
            return true;
        }

        Player player = (Player) sender;
        Rank senderRank = SpigotUtils.getRankFromSender(sender);
        
        if (cmd.getName().equalsIgnoreCase("spawn")) {
            player.teleport(plugin.getSpawnpoint().getLocation());
            player.sendMessage(ManiaUtils.color("&aTeleported you to the spawnpoint."));
        } else if (cmd.getName().equalsIgnoreCase("setspawn")) {
            if (!(senderRank.ordinal() <= Rank.ADMIN.ordinal())) {
                player.sendMessage(ManiaUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
            Spawnpoint spawnpoint = new Spawnpoint(player.getLocation());
            if (args.length > 0) {
                try {
                    spawnpoint.setRadius(Integer.parseInt(args[0]));
                } catch (NumberFormatException e) {
                    player.sendMessage("&cInvalid number for radius, using default of 0");
                    spawnpoint.setRadius(0);
                }
            }
            plugin.setSpawnpoint(spawnpoint);
            plugin.getConfig().set("spawnpoint", spawnpoint);
            plugin.saveConfig();
            player.sendMessage(ManiaUtils.color("&aYou set the spawnpoint to your current location."));
        }
        
        return true;
    }
}
