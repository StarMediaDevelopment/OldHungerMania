package net.hungermania.maniacore.api.communication;

import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.redis.RedisListener;
import net.hungermania.manialib.util.Constants;

import java.util.UUID;

public abstract class MessageHandler implements RedisListener {
    
    protected abstract void handleStaffChat(UUID player, String message);
    protected abstract void handleAdminChat(UUID player, String message);
    protected abstract void handleSpartanMsg(String server, String playerName, String hack, int violation, boolean falsePositive, double tps, int ping);
    protected abstract void handleServerSwitch(UUID player, String server);
    protected abstract void handleNetworkLeave(UUID player);
    
    public void onCommand(String cmd, String[] args) {
        if (cmd.equals("staffChat") || cmd.equals("adminChat")) {
            if (!(args.length > 1)) { return; }
            UUID uuid = UUID.fromString(args[0]);
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String message = sb.toString().trim();
            if (cmd.equals("staffChat")) {
                handleStaffChat(uuid, message);
            } else {
                handleAdminChat(uuid, message);
            }
        } else if (cmd.equals("spartanMsg")) {
            if (!(args.length == 7)) return;
            String playerName = args[0];
            String server = args[1];
            String hack = args[2];
            int violation = Integer.parseInt(args[3]);
            boolean falsePositive = Boolean.parseBoolean(args[4]);
            double tps = Double.parseDouble(args[5]);
            int ping = Integer.parseInt(args[6]);
            handleSpartanMsg(server, playerName, hack, violation, falsePositive, tps, ping);
        } else if (cmd.equals("serverSwitch")) {
            if (args.length != 2) return;
            UUID uuid = UUID.fromString(args[0]);
            String server = args[1];
            handleServerSwitch(uuid, server);
        } else if (cmd.equals("networkLeave")) {
            if (args.length != 1) return;
            UUID uuid = UUID.fromString(args[0]);
            handleNetworkLeave(uuid);
        }
    }
    
    public void sendMessage(String... command) {
        StringBuilder sb = new StringBuilder();
        for (Object s : command) {
            sb.append(s).append(" ");
        }
        Redis.sendCommand(sb.toString().trim());
    }
    public void sendStaffChatMessage(UUID uuid, String message) {
        sendMessage("staffChat", uuid.toString(), message);
    }
    
    public void sendAdminChatMessage(UUID uuid, String message) {
        sendMessage("adminChat", uuid.toString(), message);
    }
    
    public void sendSpartanMessage(String server, String playerName, String hack, int violation, boolean falsePositive, double tps, int ping) {
        sendMessage("spartanMsg", playerName, server, hack, violation + "", falsePositive + "", Constants.NUMBER_FORMAT.format(tps), ping + "");
    }
    
    public void sendChannelMessage(UUID uuid, Channel channel, String message) {
        if (channel == Channel.STAFF) {
            sendStaffChatMessage(uuid, message);
        } else if (channel == Channel.ADMIN) {
            sendAdminChatMessage(uuid, message);
        }
    }
    
    public void sendServerSwitchMessage(UUID player, String newServer) {
        sendMessage("serverSwitch", player.toString(), newServer);
    }
    
    public void sendNetworkLeaveMessage(UUID player) {
        sendMessage("networkLeave", player.toString());
    }
}