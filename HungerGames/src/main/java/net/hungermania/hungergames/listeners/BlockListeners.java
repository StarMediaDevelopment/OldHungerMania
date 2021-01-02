package net.hungermania.hungergames.listeners;

import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.team.GameTeam.Perms;
import net.hungermania.maniacore.api.util.State;
import net.hungermania.maniacore.api.util.Utils;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;

import static org.bukkit.Material.*;

public class BlockListeners extends GameListener {
    
    private static final Set<Material> ALLOWED_BREAK, NO_DROPS;
    private Map<UUID, Location> placedCraftingTables = new HashMap<>();
    static {
        ALLOWED_BREAK = new HashSet<>(Arrays.asList(LONG_GRASS, RED_ROSE, YELLOW_FLOWER, LEAVES, FIRE, WEB, MELON_BLOCK, CROPS, CARROT, POTATO));
        NO_DROPS = new HashSet<>(Arrays.asList(YELLOW_FLOWER, LONG_GRASS, COCOA, LEAVES, LEAVES_2, RED_ROSE));
    }
    
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (gameManager.getCurrentGame() != null) {
            Game game = gameManager.getCurrentGame();
            if (game.getState() == State.COUNTDOWN || game.getState() == State.DEATHMATCH_COUNTDOWN) {
                e.setCancelled(true);
                return;
            }
            
            if (e.getBlock().getType() == Material.TNT) {
                TNTPrimed entity = (TNTPrimed) e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.PRIMED_TNT);
                entity.setFuseTicks(20);
                entity.setYield(2.0F);
                EntityTNTPrimed nmsTnt = ((CraftTNTPrimed) entity).getHandle();
                try {
                    Field source = nmsTnt.getClass().getDeclaredField("source");
                    source.setAccessible(true);
                    source.set(nmsTnt, ((CraftPlayer) e.getPlayer()).getHandle()); //TODO Implement this in the kill
                } catch (Exception ex) {}
                new BukkitRunnable() {
                    public void run() {
                        e.getBlock().setType(Material.AIR);
                    }
                }.runTaskLater(plugin, 1L);
            } else if (e.getBlock().getType() == Material.WORKBENCH) {
                if (this.placedCraftingTables.containsKey(e.getPlayer().getUniqueId())) {
                    Location location = this.placedCraftingTables.get(e.getPlayer().getUniqueId());
                    if (location.getBlock() == null || location.getBlock().getType() != Material.WORKBENCH) {
                        this.placedCraftingTables.remove(e.getPlayer().getUniqueId());
                    }
                }
        
                if (this.placedCraftingTables.containsKey(e.getPlayer().getUniqueId())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ManiaUtils.color("&cYou have already placed a crafting table."));
                    return;
                }
        
                this.placedCraftingTables.put(e.getPlayer().getUniqueId(), e.getBlock().getLocation());
            } else if (e.getBlock().getType() == Material.WOOD) {
                e.setCancelled(true);
            }
        } else if (!lobby.isMapEdititing()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (gameManager.getCurrentGame() != null) {
            Game game = gameManager.getCurrentGame();
            if (game.getState() == State.COUNTDOWN || game.getState() == State.DEATHMATCH_COUNTDOWN) {
                e.setCancelled(true);
                return;
            }
    
            if (!game.getGameTeam(e.getPlayer().getUniqueId()).getPermissionValue(Perms.BREAK_BLOCKS)) {
                e.setCancelled(true);
                return;
            }
    
            if (e.getBlock().getType() == Material.WORKBENCH) {
                if (!placedCraftingTables.containsValue(e.getBlock().getLocation())) {
                    e.getPlayer().sendMessage(ManiaUtils.color("&cYou can only break crafting tables placed by a player."));
                    e.setCancelled(true);
                    return;
                }
        
                placedCraftingTables.values().removeIf(location -> location.equals(e.getBlock().getLocation()));
                return;
            }
    
            if (!ALLOWED_BREAK.contains(e.getBlock().getType())) {
                e.setCancelled(true);
                return;
            }
    
            if (NO_DROPS.contains(e.getBlock().getType())) {
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR);
            }
        } else if (!lobby.isMapEdititing()) {
            e.setCancelled(true);
        }
    }
}
