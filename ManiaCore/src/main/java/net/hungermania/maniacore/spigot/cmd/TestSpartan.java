package net.hungermania.maniacore.spigot.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestSpartan implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//        try {
//            String server = ManiaCore.getInstance().getServerManager().getCurrentServer().getName();
//            int ping = ((CraftPlayer) sender).getHandle().ping;
//            double tps = ((CraftServer) Bukkit.getServer()).getServer().recentTps[0];
//            ManiaCore.getInstance().getMessageHandler().sendSpartanMessage(server, sender.getName(), args[0], Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]), tps, ping);
//            //SpartanUtils.sendSpartanMessage(server, sender.getName(), args[0], Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]), tps, ping);
//        } catch (Exception e) {
//            e.printStackTrace();
//            sender.sendMessage(ManiaUtils.color("&cError handling that command."));
//        }
        
        return true;
    }
}
