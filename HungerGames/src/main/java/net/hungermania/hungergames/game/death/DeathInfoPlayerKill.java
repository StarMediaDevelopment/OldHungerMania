package net.hungermania.hungergames.game.death;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.hungergames.game.Game;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class DeathInfoPlayerKill extends DeathInfo {
    
    protected UUID killer;
    protected ItemStack handItem;
    protected double killerHealth;
    @Setter protected boolean mutationKill;
    private String killerTeamColor;
    
    public DeathInfoPlayerKill(UUID player, UUID killer, ItemStack handItem, double killerHealth, String killerTeamColor) {
        super(player, DeathType.PLAYER);
        this.killer = killer;
        this.handItem = handItem.clone();
        this.killerHealth = killerHealth;
        this.killerTeamColor = killerTeamColor;
    }
    
    public String getDeathMessage(Game game) {
        String killerName = killerTeamColor + game.getPlayer(killer).getUser().getName();
    
        String itemName;
        itemName = getHandItem(handItem);
    
        this.deathMessage = "&4&l>> %playername% &7was killed by " + killerName + " &7using " + itemName;
        return super.getDeathMessage(game);
    }
}
