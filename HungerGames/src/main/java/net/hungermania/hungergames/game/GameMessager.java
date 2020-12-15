package net.hungermania.hungergames.game;

import net.hungermania.hungergames.util.Messager;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GameMessager extends Messager {
    
    private Game game;
    
    public GameMessager(Game game) {
        this.game = game;
    }
    
    public void sendMessage(String message, Rank rank) {
        Set<UUID> players = new HashSet<>(game.getTributesTeam().getMembers());
        players.addAll(game.getSpectatorsTeam().getMembers());
        players.addAll(game.getMutationsTeam().getMembers());
        players.addAll(game.getHiddenStaffTeam().getMembers());
    
        for (UUID p : players) {
            Player player = Bukkit.getPlayer(p);
            if (player != null) {
                User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
                if (rank != null) {
                    if (user.hasPermission(rank)) {
                        user.sendMessage(message);
                    }
                } else {
                    user.sendMessage(message);
                }
            }
        }
    }
}
