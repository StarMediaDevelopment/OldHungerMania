package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.leveling.Level;
import net.hungermania.manialib.sql.*;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class LevelRecord implements IRecord<Level> {
    
    private Level level;
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "levels");
        table.addColumn(new Column("number", DataType.INT, true, true));
        table.addColumn(new Column("totalXp", DataType.INT));
        table.addColumn(new Column("coinReward", DataType.INT));
        table.addColumn(new Column("numberColor", DataType.VARCHAR, 20));
        return table;
    }
    
    public LevelRecord(Level level) {
        this.level = level;
    }
    
    public LevelRecord(Row row) {
        this.level = Level.builder().number(row.getInt("number")).totalXp(row.getInt("totalXp")).coinReward(row.getInt("coinReward")).numberColor(ChatColor.valueOf(row.getString("numberColor"))).build();
    }
    
    @Override
    public int getId() {
        return level.getNumber();
    }
    
    @Override
    public void setId(int id) {
        level.setNumber(id);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>(){{
            put("number", level.getNumber());
            put("totalXp", level.getTotalXp());
            put("coinReward", level.getCoinReward());
            put("numberColor", level.getNumberColor().name());
        }};
    }
    
    @Override
    public Level toObject() {
        return level;
    }
}
