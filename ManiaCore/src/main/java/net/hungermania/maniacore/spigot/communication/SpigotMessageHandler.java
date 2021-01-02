package net.hungermania.maniacore.spigot.communication;

import net.hungermania.maniacore.ManiaCorePlugin;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.communication.MessageHandler;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.maniacore.spigot.util.SpartanUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class SpigotMessageHandler extends MessageHandler {
    
    private ManiaCorePlugin plugin;
    
    public SpigotMessageHandler(ManiaCorePlugin plugin) {
        this.plugin = plugin;
    }
    
    protected void handleStaffChat(UUID p, String message) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        Channel c = Channel.STAFF;
        StringBuilder format = new StringBuilder().append(c.getChatPrefix()).append(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
            if (user.hasPermission(Rank.HELPER)) {
                if (user.getToggle(Toggles.STAFF_NOTIFICATIONS).getAsBoolean()) {
                    player.sendMessage(ManiaManiaUtils.color(format.toString()));
                }
            }
        }
    }
    
    protected void handleAdminChat(UUID p, String message) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        Channel c = Channel.ADMIN;
        StringBuilder format = new StringBuilder().append(c.getChatPrefix()).append(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
            if (user.hasPermission(Rank.ADMIN)) {
                if (user.getToggle(Toggles.ADMIN_NOTIFICATIONS).getAsBoolean()) {
                    player.sendMessage(ManiaManiaUtils.color(format.toString()));
                }
            }
        }
    }
    
    protected void handleSpartanMsg(String server, String playerName, String hack, int violation, boolean falsePositive, double tps, int ping) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        SpartanUtils.sendSpartanMessage(server, playerName, hack, violation, falsePositive, tps, ping);
    }
    
    protected void handleServerSwitch(UUID p, String server) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        SpigotUser user = (SpigotUser) plugin.getManiaCore().getUserManager().getUser(p);
        if (user.hasPermission(Rank.MEDIA)) {
            String format = ManiaManiaUtils.color(Channel.STAFF.getChatPrefix() + user.getColoredName() + " &7&l-> &6" + server);
            for (Player player : Bukkit.getOnlinePlayers()) {
                User u = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
                if (u.hasPermission(Rank.HELPER)) {
                    if (u.getToggle(Toggles.STAFF_NOTIFICATIONS).getAsBoolean()) {
                        player.sendMessage(format);
                    }
                }
            }
        }
    }
    
    protected void handleNetworkLeave(UUID p) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        SpigotUser user = new SpigotUser(plugin.getManiaCore().getUserManager().getUser(p));
        if (user.hasPermission(Rank.MEDIA)) {
            String format = ManiaManiaUtils.color(Channel.STAFF.getChatPrefix() + user.getColoredName() + " &6left the network.");
            for (Player player : Bukkit.getOnlinePlayers()) {
                User u = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
                if (u.hasPermission(Rank.HELPER)) {
                    if (u.getToggle(Toggles.STAFF_NOTIFICATIONS).getAsBoolean()) {
                        player.sendMessage(format);
                    }
                }
            }
        }
    }
}