package net.hungermania.maniacore.bungee.cmd;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.bungee.util.BungeeUtils;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

public class GotoCmd extends Command {
    public GotoCmd() {
        super("goto");
    }
    
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            BungeeUtils.sendMessage(sender, "Only players may use that command", ChatColor.RED);
            return;
        }
        
        if (!(args.length > 0)) {
            BungeeUtils.sendMessage(sender, "You must provide arguments", ChatColor.RED);
            return;
        }
        
        ProxiedPlayer player = (ProxiedPlayer) sender;
    
        User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
        if (!user.hasPermission(Rank.HELPER)) {
            BungeeUtils.sendMessage(sender, "You do not have permission to use that command", ChatColor.RED);
            return;
        }
    
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        if (args[0].startsWith("s:")) {
            String[] split = args[0].split(":");
            String server = split[1];
            ServerInfo serverInfo = null;
            for (ServerInfo si : servers.values()) {
                if (si.getName().equalsIgnoreCase(server)) {
                    serverInfo = si;
                    break;
                }
            }
            
            if (serverInfo == null) {
                BungeeUtils.sendMessage(sender, "You provided an invalid server name.", ChatColor.RED);
                return;
            }
    
            if (!serverInfo.canAccess(sender)) {
                BungeeUtils.sendMessage(sender, "You cannot access that server.", ChatColor.RED);
                return;
            }
    
            ServerInfo finalServerInfo = serverInfo;
            ((ProxiedPlayer) sender).connect(serverInfo, (status, throwable) -> {
                if (throwable != null) {
                    BungeeUtils.sendMessage(sender, "There was an error connecting to that server: " + throwable.getMessage(), ChatColor.RED);
                } else {
                    BungeeUtils.sendMessage(sender, "Sending you to " + finalServerInfo.getName(), ChatColor.GREEN);
                }
            }, Reason.COMMAND);
        } else if (args[0].startsWith("p:")) {
            String[] split = args[0].split(":");
            String playerName = split[1];
            ProxiedPlayer target = null;
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                if (p.getName().equalsIgnoreCase(playerName)) {
                    target = p;
                    break;
                }
            }
            
            if (target == null) {
                BungeeUtils.sendMessage(sender, "Could not find an online player with that name.", ChatColor.RED);
                return;
            }
            
            ServerInfo serverInfo = target.getServer().getInfo();
            ((ProxiedPlayer) sender).connect(serverInfo, (status, throwable) -> {
                if (throwable != null) {
                    BungeeUtils.sendMessage(sender, "There was an error connecting to that server: " + throwable.getMessage(), ChatColor.RED);
                } else {
                    BungeeUtils.sendMessage(sender, "Sending you to " + serverInfo.getName(), ChatColor.GREEN);
                }
            }, Reason.COMMAND);
        }
    }
}
