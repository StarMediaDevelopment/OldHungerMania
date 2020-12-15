package net.hungermania.hungergames.game;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.util.State;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerTrackerTask extends BukkitRunnable {
    
    private Game game;
    
    public PlayerTrackerTask(Game game) {
        this.game = game;
    }
    
    public void run() {
        long start = System.currentTimeMillis();
        if (game == null) {
            cancel();
            return;
        }
        
        if (!(game.getState() == State.PLAYING || game.getState() == State.PLAYING_DEATHMATCH || game.getState() == State.DEATHMATCH || game.getState() == State.DEATHMATCH_COUNTDOWN)) {
            return;
        }
        
        Set<UUID> players = new HashSet<>(game.getTributesTeam().getMembers());
        players.addAll(game.getMutationsTeam().getMembers());
        
        for (UUID p : players) {
            Player player = Bukkit.getPlayer(p);
            boolean trackerInHotbar = false;
            boolean holdingTracker = false;
            for (int i = 0; i < 9; i++) {
                if (!trackerInHotbar) {
                    ItemStack item = player.getInventory().getItem(i);
                    if (item == null) { continue; }
                    trackerInHotbar = item.getType() == Material.COMPASS;
                }
            }
            
            if (trackerInHotbar) {
                ItemStack hand = player.getInventory().getItemInHand();
                if (hand != null) {
                    holdingTracker = hand.getType() == Material.COMPASS;
                }
            }
            
            Player target = null;
            double distance = -1;
            GamePlayer gamePlayer = game.getPlayer(p);
            if (game.getMutationsTeam().isMember(p)) {
                target = Bukkit.getPlayer(gamePlayer.getMutationTarget());
            } else {
                for (UUID u : game.getTributesTeam()) {
                    Player t = Bukkit.getPlayer(u);
                    if (u.equals(p)) { continue; }
                    double pd = player.getLocation().distance(t.getLocation());
                    if (target == null) {
                        target = t;
                        distance = pd;
                    } else if (distance == -1) {
                        target = t;
                        distance = pd;
                    } else {
                        if (pd < distance) {
                            target = t;
                            distance = pd;
                        }
                    }
                }
            }
            
            if (target == null) {
                continue;
            }
            
            if (distance == -1) {
                distance = player.getLocation().distance(target.getLocation());
            }
            
            Player finalClosest = target;
            new BukkitRunnable() {
                public void run() {
                    player.setCompassTarget(finalClosest.getLocation());
                }
            }.runTask(HungerGames.getInstance());
            
            if (holdingTracker) {
                SpigotUtils.sendActionBar(player, "&f&lTARGET: &a" + target.getName() + " &7| &f&lDISTANCE: &a" + ((int) distance) + "m");
            }
        }
        
        long end = System.currentTimeMillis();
        long totalTime = end - start;
        if (totalTime > 20) {
            ManiaCore.getInstance().getLogger().severe("Player Tracker task took " + totalTime);
        }
        
    }
    
    public PlayerTrackerTask start() {
        runTaskTimerAsynchronously(HungerGames.getInstance(), 20L, 2L);
        return this;
    }
}
