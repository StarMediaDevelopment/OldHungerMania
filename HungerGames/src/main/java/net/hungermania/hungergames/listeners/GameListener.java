package net.hungermania.hungergames.listeners;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.GameManager;
import net.hungermania.hungergames.lobby.Lobby;
import org.bukkit.event.Listener;

public abstract class GameListener implements Listener {
    protected HungerGames plugin;
    protected GameManager gameManager;
    protected Lobby lobby;
    
    public GameListener() {
        plugin = HungerGames.getInstance();
        gameManager = HungerGames.getInstance().getGameManager();
        lobby = HungerGames.getInstance().getLobby();
    }
}
