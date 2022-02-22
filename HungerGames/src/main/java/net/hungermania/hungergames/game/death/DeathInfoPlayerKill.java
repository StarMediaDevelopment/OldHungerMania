package net.hungermania.hungergames.game.death;

import net.hungermania.hungergames.game.Game;
import net.hungermania.maniacore.api.user.User;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;


public class DeathInfoPlayerKill extends DeathInfo {
    
    protected UUID killer;
    protected ItemStack handItem;
    protected double killerHealth;
     protected boolean mutationKill;
    private String killerTeamColor;
    
    public DeathInfoPlayerKill(UUID player, UUID killer, ItemStack handItem, double killerHealth, String killerTeamColor) {
        super(player, DeathType.PLAYER);
        this.killer = killer;
        this.handItem = handItem.clone();
        this.killerHealth = killerHealth;
        this.killerTeamColor = killerTeamColor;
    }
    
    public String getDeathMessage(Game game) {
        String killerName = killerTeamColor;
        User user = game.getPlayer(killer).getUser();
        if (user.getNickname() != null && user.getNickname().isActive()) {
            killerName += user.getNickname().getName();
        } else {
            killerName += user.getName();
        }
        
        String itemName;
        itemName = getHandItem(handItem);
    
        this.deathMessage = "&4&l>> %playername% &7was killed by " + killerName + " &7using " + itemName;
        return super.getDeathMessage(game);
    }
    
    public UUID getKiller() {
        return killer;
    }
    
    public ItemStack getHandItem() {
        return handItem;
    }
    
    public double getKillerHealth() {
        return killerHealth;
    }
    
    public boolean isMutationKill() {
        return mutationKill;
    }
    
    public String getKillerTeamColor() {
        return killerTeamColor;
    }
    
    public void setKiller(UUID killer) {
        this.killer = killer;
    }
    
    public void setHandItem(ItemStack handItem) {
        this.handItem = handItem;
    }
    
    public void setKillerHealth(double killerHealth) {
        this.killerHealth = killerHealth;
    }
    
    public void setMutationKill(boolean mutationKill) {
        this.mutationKill = mutationKill;
    }
    
    public void setKillerTeamColor(String killerTeamColor) {
        this.killerTeamColor = killerTeamColor;
    }
}
