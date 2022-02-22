package net.hungermania.hungergames.game;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.records.GameRecord;
import net.hungermania.manialib.sql.IRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {
    
    private Map<Integer, Game> games = new HashMap<>();
    private Game currentGame;
    private HungerGames plugin;
    private int gameCounter = 0;
    
    public GameManager(HungerGames plugin) {
        this.plugin = plugin;
    }
    
    public Game getGame(int id) {
        if (this.games.containsKey(id)) {
            return this.games.get(id);
        }
        
        List<IRecord> records = plugin.getManiaCore().getDatabase().getRecords(GameRecord.class, "id", id);
        if (records.size() > 0) {
            for (IRecord record : records) {
                if (record instanceof GameRecord) {
                    GameRecord gameRecord = (GameRecord) record;
                    if (gameRecord.getId() == id) {
                        this.games.put(id, gameRecord.toObject());
                        return gameRecord.toObject();
                    }
                }
            }
        }
        
        return null;
    }
    
    public Game getCurrentGame() {
        return currentGame;
    }
    
    public Map<Integer, Game> getGames() {
        return games;
    }
    
    public void setGames(Map<Integer, Game> games) {
        this.games = games;
    }
    
    public HungerGames getPlugin() {
        return plugin;
    }
    
    public void setPlugin(HungerGames plugin) {
        this.plugin = plugin;
    }
    
    public int getGameCounter() {
        return gameCounter;
    }
    
    public void setGameCounter(int gameCounter) {
        this.gameCounter = gameCounter;
    }
    
    public void setCurrentGame(Game currentGame) {
        if (currentGame != null) {
            if (!this.games.containsKey(currentGame.getId())) {
                this.games.put(currentGame.getId(), currentGame);
            }
        }
        this.currentGame = currentGame;
    }
}