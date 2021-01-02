package net.hungermania.maniacore.spigot.cmd;

import net.hungermania.maniacore.ManiaCorePlugin;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.records.UserRecord;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.user.toggle.Toggle;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.events.UserIncognitoEvent;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class IncognitoCmd implements CommandExecutor {
    
    private ManiaCorePlugin plugin;
    
    public IncognitoCmd(ManiaCorePlugin plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ManiaManiaUtils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = (Player) sender;
        User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
        if (!user.hasPermission(Rank.HELPER)) {
            player.sendMessage(ManiaManiaUtils.color("&cYou do not have permission to use that command."));
            return true;
        }
        
        Map<UUID, Boolean> affectedPlayers = new HashMap<>();
        Toggle incognito = user.getToggle(Toggles.INCOGNITO);
        for (Player p : Bukkit.getOnlinePlayers()) {
            User u = ManiaCore.getInstance().getUserManager().getUser(p.getUniqueId());
            if (incognito.getAsBoolean()) {
                affectedPlayers.put(p.getUniqueId(), true);
            } else {
                affectedPlayers.put(p.getUniqueId(), u.hasPermission(Rank.HELPER));
            }
        }
    
        UserIncognitoEvent event = new UserIncognitoEvent(((SpigotUser) user), incognito.getAsBoolean(), !incognito.getAsBoolean(), affectedPlayers);
        plugin.getServer().getPluginManager().callEvent(event);
        
        if (event.isCancelled()) {
            player.sendMessage(ManiaManiaUtils.color("&cCould not complete incognito command for the reason " + event.getCancelledReason()));
            return true;
        }
        
        affectedPlayers = event.getAffectedPlayers();
        incognito.setValue((!incognito.getAsBoolean()) + "");
    
        if (incognito.getAsBoolean()) {
            player.sendMessage(ManiaManiaUtils.color("&6&l>> &9You are &b&lINCOGNITO&9, nobody can see you and your whereabouts aren't being reported."));
        } else {
            player.sendMessage(ManiaManiaUtils.color("&6&l>> &cYou are no longer &b&lINCOGNITO&c."));
        }
        
        for (Entry<UUID, Boolean> entry : affectedPlayers.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if (incognito.getAsBoolean()) {
                if (!entry.getValue()) {
                    p.hidePlayer(player);
                }
            } else {
                if (entry.getValue()) {
                    p.showPlayer(player);
                }
            }
        }
        
        new UserRecord(user).push(plugin.getManiaDatabase());
        return true;
    }
}
