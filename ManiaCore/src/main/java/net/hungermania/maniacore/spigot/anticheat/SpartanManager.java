package net.hungermania.maniacore.spigot.anticheat;

import me.vagdedes.spartan.api.PlayerViolationEvent;
import net.hungermania.maniacore.api.ManiaCore;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpartanManager implements Listener {
    
    @EventHandler
    public void onSpartanViolation(PlayerViolationEvent e) {
        String server = ManiaCore.getInstance().getServerManager().getCurrentServer().getName();
        int ping = ((CraftPlayer) e.getPlayer()).getHandle().ping;
        double tps = ((CraftServer) Bukkit.getServer()).getServer().recentTps[0];
        ManiaCore.getInstance().getMessageHandler().sendSpartanMessage(server, e.getPlayer().getName(), e.getHackType().name(), e.getViolation(), e.isFalsePositive(), tps, ping);
    }
}
