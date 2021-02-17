package net.hungermania.hungergames.game.death;

import net.hungermania.hungergames.game.Game;
import net.hungermania.maniacore.spigot.user.SpigotUser;

import java.util.UUID;

public class DeathInfoKilledSuicide extends DeathInfo {
    
    private UUID killer;
    private String killerTeamColor;
    
    public DeathInfoKilledSuicide(UUID player, UUID killer, String killerTeamColor) {
        super(player, DeathType.SUICIDE_KILLED);
        this.killer = killer;
        this.killerTeamColor = killerTeamColor;
    }
    
    public UUID getKiller() {
        return killer;
    }
    
    @Override
    public String getDeathMessage(Game game) {
        String killerName = killerTeamColor;
        SpigotUser user = game.getPlayer(killer).getUser();
        if (user.getNickname() != null || user.getNickname().isActive()) {
            killerName += user.getNickname().getName();
        } else {
            killerName += user.getName();
        }
        this.deathMessage = "&4&l>> %playername% &7was killed by " + killerName + "&7's suicide.";
        return super.getDeathMessage(game);
    }
}