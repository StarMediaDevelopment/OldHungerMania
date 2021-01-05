package net.hungermania.hungergames.records;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.game.Game;
import net.hungermania.hungergames.map.HGMap;
import net.hungermania.manialib.sql.*;
import net.hungermania.manialib.util.Utils;

import java.util.*;

public class GameRecord implements IRecord<Game> {
    
    private Game game;
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "hg_games");
        Column id = new Column("id", DataType.INT, true, true);
        Column mapName = new Column("mapName", DataType.VARCHAR, 50);
        Column gameStart = new Column("gameStart", DataType.BIGINT);
        Column gameEnd = new Column("gameEnd", DataType.BIGINT);
        Column profiles = new Column("profiles", DataType.VARCHAR, 500);
        Column archived = new Column("archived", DataType.VARCHAR, 5);
        Column serverId = new Column("serverId", DataType.INT);
        table.addColumns(id, mapName, gameStart, gameEnd, profiles, archived, serverId);
        return table;
    }
    
    public GameRecord(Game game) {
        this.game = game;
    }
    
    public GameRecord(Row row) {
        int id = row.getInt("id");
        String mapName = row.getString("mapName");
        long gameStart = row.getLong("gameStart");
        long gameEnd = row.getLong("gameEnd");
        boolean archived = row.getBoolean("archived");
        String rawProfiles = row.getString("profiles");
        Set<Integer> profiles = new HashSet<>();
        String[] profilesArray = rawProfiles.split(",");
        for (String p : profilesArray) {
            try {
                Integer profile = Integer.parseInt(p);
                profiles.add(profile);
            } catch (NumberFormatException e) {
                
            }
        }
        HGMap hgMap = HungerGames.getInstance().getMapManager().getMaps().get(mapName.toLowerCase().replace(" ", "_"));
        int serverId = row.getInt("serverId");
        this.game = new Game(id, hgMap, gameStart, gameEnd, archived, profiles, serverId);
    }
    
    
    public int getId() {
        return game.getId();
    }
    
    public void setId(int id) {
        game.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", game.getId());
            put("mapName", game.getMap().getName().replace("'", "''"));
            put("gameStart", game.getGameStart());
            put("gameEnd", game.getGameEnd());
            put("archived", game.isArchived());
            put("profiles", Utils.join(game.getArchivedProfiles(), ","));
            put("serverId", game.getServerId());
        }};
    }
    
    public Game toObject() {
        return game;
    }
}