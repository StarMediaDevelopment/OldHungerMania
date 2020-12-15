package net.hungermania.hungergames.game.team;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.game.PlayerType;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

public class MutationsTeam extends GameTeam {
    public MutationsTeam(Game game) {
        super("Mutations", "&d", PlayerType.MUTATION, game);
        this.permissons.put(Perms.ALWAYS_MAX_FOOD, true);
        this.permissons.put(Perms.DAMAGE, true);
        this.setGameMode(GameMode.ADVENTURE);
    }
    
    public void join(UUID uuid) {
        game.addPlayer(uuid);
        User user = HungerGames.getInstance().getManiaCore().getUserManager().getUser(uuid);
        Player player = Bukkit.getPlayer(uuid);
        setPlayerStats(player, true, false, false);
        this.members.add(uuid);
        user.sendMessage(getJoinMessage());
        player.setPlayerListName(Utils.color(getColor() + player.getName()));
        Set<UUID> shown = new HashSet<>(game.getTributesTeam().getMembers());
        shown.addAll(game.getMutationsTeam().getMembers());
        for (UUID gp : shown) {
            Player p = Bukkit.getPlayer(gp);
            player.showPlayer(p);
            p.showPlayer(player);
        }
    
        Set<UUID> hidden = new HashSet<>(game.getSpectatorsTeam().getMembers());
        hidden.addAll(game.getHiddenStaffTeam().getMembers());
        for (UUID s : hidden) {
            Player spectator = Bukkit.getPlayer(s);
            player.hidePlayer(spectator);
            spectator.showPlayer(player);
        }
    }
}
