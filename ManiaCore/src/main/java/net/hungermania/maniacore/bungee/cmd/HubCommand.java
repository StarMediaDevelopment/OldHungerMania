package net.hungermania.maniacore.bungee.cmd;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {
    public HubCommand() {
        super("hub");
    }
    
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("This command can only be run by a player!").color(ChatColor.RED).create());
            return;
        }
        
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (player.getServer().getInfo().getName().toLowerCase().contains("hub")) {
            player.sendMessage(new ComponentBuilder("You are already connected to the Hub!").color(ChatColor.RED).create());
            return;
        }
    
        ServerInfo target = ProxyServer.getInstance().getServerInfo("Hub");
//        for (ServerObject hub : TimoCloudAPI.getUniversalAPI().getServerGroup("Hub").getServers()) {
//            if (hub.getState().equalsIgnoreCase("online")) {
//                if (hub.getOnlinePlayerCount() < hub.getMaxPlayerCount()) {
//                    target = ProxyServer.getInstance().getServerInfo(hub.getName());
//                }
//            }
//        }
        
        if (target == null) {
            player.sendMessage(new ComponentBuilder("Could not find a hub to connect you to.").color(ChatColor.RED).create());
            return;
        }
        player.connect(target, (result, error) -> {
            if (error != null) {
                player.sendMessage(new ComponentBuilder("There was an error connecting to that server: " + error.getMessage()).color(ChatColor.RED).create());
                return;
            }
            
            if (!result) {
                player.sendMessage(new ComponentBuilder("There was an unknown error connecting to that server.").color(ChatColor.RED).create());
            }
        });
    }
}
