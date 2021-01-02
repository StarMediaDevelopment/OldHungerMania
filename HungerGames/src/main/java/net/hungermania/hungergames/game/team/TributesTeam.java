package net.hungermania.hungergames.game.team;

import net.hungermania.hungergames.game.*;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.State;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class TributesTeam extends GameTeam {
    public TributesTeam(Game game) {
        super("Tributes", "&a", PlayerType.TRIBUTE, game);
        setGameMode(GameMode.SURVIVAL);
        this.permissons.put(Perms.ALLOWED_TO_DROP, true);
        this.permissons.put(Perms.BREAK_BLOCKS, true);
        this.permissons.put(Perms.OPEN_ENCHANT, true);
        this.permissons.put(Perms.ALLOWED_TO_PICKUP, true);
        this.permissons.put(Perms.DAMAGE, true);
        //TODO List of Game teams for seeable and unseeable
    }
    
    public void join(UUID uuid) {
        game.addPlayer(uuid);
        Player player = Bukkit.getPlayer(uuid);
        User user = ManiaCore.getInstance().getUserManager().getUser(uuid);
        for (PotionEffect activePotionEffect : new ArrayList<>(player.getActivePotionEffects())) {
            player.removePotionEffect(activePotionEffect.getType());
        }
    
        player.setPlayerListName(ManiaUtils.color(getColor() + player.getName()));
        player.spigot().setCollidesWithEntities(true);
        
        this.members.add(player.getUniqueId());
        user.sendMessage(getJoinMessage());
        Set<UUID> seeable = new HashSet<>(game.getTributesTeam().getMembers());
        members.addAll(game.getMutationsTeam().getMembers());
        for (UUID t : seeable) {
            Player tribute = Bukkit.getPlayer(t);
            player.showPlayer(tribute);
            tribute.showPlayer(player);
        }
        
        Set<UUID> unseeable = new HashSet<>(game.getSpectatorsTeam().getMembers());
        unseeable.addAll(game.getHiddenStaffTeam().getMembers());
        
        for (UUID s : unseeable) {
            Player spectator = Bukkit.getPlayer(s);
            player.hidePlayer(spectator);
            spectator.showPlayer(player);
        }
        
        State state = game.getState();
        GamePlayer gamePlayer = game.getPlayer(uuid);
        if (!(state == State.SETUP || state == State.DEATHMATCH || state == State.ENDING)) {
            if (!gamePlayer.hasMutated()) {
                Location spawn = null;
                spawn = getSpawn(user, spawn);
                
                if (spawn == null) {
                    spawn = getSpawn(user, spawn);
                    if (spawn == null) {
                        spawn = SpigotUtils.positionToLocation(game.getMap().getWorld(), game.getMap().getSpawns().get(0));
                    }
                }
                
                if (spawn != null) {
                    player.teleport(spawn);
                    setPlayerStats(player, true, false, false);
                } else {
                    user.sendMessage("&c&l>> Could not find a spawn location for you. You have been set as a spectator.");
                    leave(uuid);
                    game.getSpectatorsTeam().join(uuid);
                }
            } else {
                setPlayerStats(player, true, false, false);
            }
        }
    }
}
