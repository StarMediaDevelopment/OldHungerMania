package net.hungermania.hungergames.records;

import net.hungermania.hungergames.settings.GameSettings;
import net.hungermania.hungergames.settings.Time;
import net.hungermania.hungergames.settings.Weather;
import net.hungermania.manialib.sql.*;

import java.util.HashMap;
import java.util.Map;

public class GameSettingsRecord implements IRecord<GameSettings> {
    
    private GameSettings gameSettings;
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "game_settings");
        
        Column id = new Column("id", DataType.INT, true, true);
        Column name = new Column("name", DataType.VARCHAR, 64);
        Column maxPlayers = new Column("maxPlayers", DataType.INT);
        Column minPlayers = new Column("minPlayers", DataType.INT);
        Column startTimer = new Column("startTimer", DataType.INT);
        Column maxHealth = new Column("maxHealth", DataType.INT);
        Column gracePeriodLength = new Column("gracePeriodLength", DataType.INT);
        Column maxGames = new Column("maxGames", DataType.INT);
        Column gameLength = new Column("gameLength", DataType.INT);
        Column maxMapOptions = new Column("maxMapOptions", DataType.INT);
        Column deathmatchLength = new Column("deathmatchLength", DataType.INT);
        Column startingCountdown = new Column("startingCountdown", DataType.INT);
        Column deathmatchPlayerStart = new Column("deathmatchPlayerStart", DataType.INT);
        Column pearlCooldown = new Column("pearlCooldown", DataType.INT);
        Column mutationDelay = new Column("mutationDelay", DataType.INT);
        Column nextGameStart = new Column("nextGameStart", DataType.INT);
        Column mutations = new Column("mutations", DataType.VARCHAR, 5);
        Column regeneration = new Column("regeneration", DataType.VARCHAR, 5);
        Column gracePeriod = new Column("gracePeriod", DataType.VARCHAR, 5);
        Column unlimitedPasses = new Column("unlimitedPasses", DataType.VARCHAR, 5);
        Column timeProgression = new Column("timeProgression", DataType.VARCHAR, 5);
        Column weatherProgression = new Column("weatherProgression", DataType.VARCHAR, 5);
        Column coinMultiplier = new Column("coinMultiplier", DataType.VARCHAR, 5);
        Column voteWeight = new Column("voteWeight", DataType.VARCHAR, 5);
        Column time = new Column("time", DataType.VARCHAR, 10);
        Column weather = new Column("weather", DataType.VARCHAR, 10);
        table.addColumns(id, name, maxPlayers, minPlayers, startTimer, maxHealth, gracePeriodLength, maxGames, gameLength, maxMapOptions, deathmatchLength, startingCountdown, deathmatchPlayerStart, pearlCooldown, mutationDelay, nextGameStart, mutations, regeneration, gracePeriod, unlimitedPasses, timeProgression, weatherProgression, coinMultiplier, voteWeight, time, weather);
        return table;
    }
    
    public GameSettingsRecord(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }
    
    public GameSettingsRecord(Row row) {
        int id = row.getInt("id");
        String name = row.getString("name");
        int maxPlayers = row.getInt("maxPlayers");
        int minPlayers = row.getInt("minPlayers");
        int startTimer = row.getInt("startTimer");
        int maxHealth = row.getInt("maxHealth");
        int gracePeriodLength = row.getInt("gracePeriodLength");
        int maxGames = row.getInt("maxGames");
        int maxMapOptions = row.getInt("maxMapOptions");
        boolean mutations = row.getBoolean("mutations");
        boolean regeneration = row.getBoolean("regeneration");
        boolean gracePeriod = row.getBoolean("gracePeriod");
        boolean unlimitedPasses = row.getBoolean("unlimitedPasses");
        boolean timeProgression = row.getBoolean("timeProgression");
        boolean weatherProgression = row.getBoolean("weatherProgression");
        Time time = Time.valueOf(row.getString("time"));
        Weather weather = Weather.valueOf(row.getString("weather"));
        int gameLength = row.getInt("gameLength");
        int deathmatchLength = row.getInt("deathmatchLength");
        int startingCountdown = row.getInt("startingCountdown");
        int deathmatchPlayerStart = row.getInt("deathmatchPlayerStart");
        int pearlCooldown = row.getInt("pearlCooldown");
        int mutationDelay = row.getInt("mutationDelay");
        int nextGameStart = row.getInt("nextGameStart");
        boolean coinMultiplier = row.getBoolean("coinMultiplier");
        boolean voteWeight = row.getBoolean("voteWeight");
        this.gameSettings = new GameSettings(id, name, maxPlayers, minPlayers, startTimer, maxHealth, gracePeriodLength, maxGames, gameLength, maxMapOptions, deathmatchLength, startingCountdown, deathmatchPlayerStart, pearlCooldown, mutationDelay, nextGameStart, mutations, regeneration, gracePeriod, unlimitedPasses, timeProgression, weatherProgression, coinMultiplier, voteWeight, time, weather);
    }
    
    public int getId() {
        return gameSettings.getId();
    }
    
    public void setId(int id) {
        gameSettings.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", gameSettings.getId());
            put("name", gameSettings.getName());
            put("maxPlayers", gameSettings.getMaxPlayers());
            put("minPlayers", gameSettings.getMinPlayers());
            put("startTimer", gameSettings.getStartTimer());
            put("maxHealth", gameSettings.getMaxHealth());
            put("gracePeriodLength", gameSettings.getGracePeriodLength());
            put("maxGames", gameSettings.getMaxGames());
            put("gameLength", gameSettings.getGameLength());
            put("maxMapOptions", gameSettings.getMaxMapOptions());
            put("mutations", gameSettings.isMutations());
            put("regeneration", gameSettings.isRegeneration());
            put("gracePeriod", gameSettings.isGracePeriod());
            put("unlimitedPasses", gameSettings.isUnlimitedPasses());
            put("timeProgression", gameSettings.isTimeProgression());
            put("weatherProgression", gameSettings.isWeatherProgression());
            put("time", gameSettings.getTime().name());
            put("weather", gameSettings.getWeather().name());
            put("deathmatchLength", gameSettings.getDeathmatchLength());
            put("startingCountdown", gameSettings.getStartingCountdown());
            put("deathmatchPlayerStart", gameSettings.getDeathmatchPlayerStart());
            put("pearlCooldown", gameSettings.getPearlCooldown());
            put("mutationDelay", gameSettings.getMutationDelay());
            put("nextGameStart", gameSettings.getNextGameStart());
            put("coinMultiplier", gameSettings.isCoinMultiplier());
            put("voteWeight", gameSettings.isVoteWeight());
        }};
    }
    
    public GameSettings toObject() {
        return gameSettings;
    }
}