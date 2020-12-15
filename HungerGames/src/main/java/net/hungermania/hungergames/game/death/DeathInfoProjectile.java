package net.hungermania.hungergames.game.death;

import lombok.Getter;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class DeathInfoProjectile extends DeathInfo {
    
    private Entity shooter;
    private double distance;
    private String killerTeamColor;
    
    public DeathInfoProjectile(UUID player, Entity shooter, double v, String teamColor) {
        super(player, DeathType.PROJECTILE);
        this.shooter = shooter;
        distance = v;
        this.killerTeamColor = teamColor;
    }
    
    public String getDeathMessage(Game game) {
        String killerName;
        if (shooter instanceof Player) {
            Player playerShooter = (Player) shooter;
            SpigotUser spigotUser = (SpigotUser) HungerGames.getInstance().getManiaCore().getUserManager().getUser(playerShooter.getUniqueId());
            killerName = killerTeamColor + spigotUser.getName();
        } else {
            killerName = "&f" + Utils.capitalizeEveryWord(shooter.getType().name());
        }
        
        this.deathMessage = "&4&l>> %playername% &7was shot by " + killerName + " &7from &f" + Utils.formatNumber(distance) + " blocks.";
        return super.getDeathMessage(game);
    }
}
