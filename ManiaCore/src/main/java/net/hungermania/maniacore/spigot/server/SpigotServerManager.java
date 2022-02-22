package net.hungermania.maniacore.spigot.server;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.server.ManiaServer;
import net.hungermania.maniacore.api.server.ServerManager;
import net.hungermania.maniacore.api.server.ServerType;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpigotServerManager extends ServerManager {
    public SpigotServerManager(ManiaCore maniaCore) {
        super(maniaCore);
    }

    @Override
    public void init() {
//        ServerObject server = TimoCloudAPI.getBukkitAPI().getThisServer();
//        this.currentServer = new ManiaServer(server.getName(), server.getPort());
//        this.currentServer.setNetworkType(networkType); //TODO
    }
    
    protected void handleServerStart(String server) {
        Channel c = Channel.STAFF;
        StringBuilder format = new StringBuilder().append(c.getChatPrefix()).append(server).append(" has started.");
        handleServerMessage(c, format);
    }
    
    protected void handleGameReady(String server) {
        if (getCurrentServer().getType() == ServerType.HUB) {
            String message = "&6&l>> &a&lA game is ready at the server " + server + "!";
            //TODO Click text
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(ManiaUtils.color(message));
            }
        }
    }
    
    protected void handleServerStop(String server) {
        Channel c = Channel.STAFF;
        StringBuilder format = new StringBuilder().append(c.getChatPrefix()).append(server).append(" has stopped.");
        handleServerMessage(c, format);
    }
    
    private void handleServerMessage(Channel c, StringBuilder format) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(c.getPermission())) {
                User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
                if (user.getToggle(Toggles.STAFF_NOTIFICATIONS).getAsBoolean()) {
                    player.sendMessage(ManiaUtils.color(format.toString()));
                }
            }
        }
    }
}
