package net.hungermania.hungergames.game.death;

import net.hungermania.hungergames.game.Game;

import java.util.UUID;

public class DeathInfoSuicide extends DeathInfo {
    public DeathInfoSuicide(UUID player) {
        super(player, DeathType.SUICIDE);
    }
    
    @Override
    public String getDeathMessage(Game game) {
        this.deathMessage = "&4&l>> %playername% &7suicided";
        return super.getDeathMessage(game);
    }
}