package net.hungermania.hungergames.records;

import net.hungermania.hungergames.loot.Loot;
import net.hungermania.manialib.sql.*;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class LootRecord implements IRecord<Loot> {
    
    private Loot loot;
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "loot");
        Column id = new Column("id", DataType.INT, true, true);
        Column material = new Column("material", DataType.VARCHAR, 50);
        Column name = new Column("name", DataType.VARCHAR, 100);
        Column weight = new Column("weight", DataType.INT);
        Column maxAmount = new Column("maxAmount", DataType.INT);
        table.addColumns(id, material, name, weight, maxAmount);
        return table;
    }
    
    public LootRecord(Loot loot) {
        this.loot = loot;
    }
    
    public LootRecord(Row row) {
        int id = row.getInt("id");
        Material material = Material.valueOf(row.getString("material"));
        String name = row.getString("name");
        int weight = row.getInt("weight");
        int maxAmount = row.getInt("maxAmount");
        this.loot = new Loot(id, material, name, weight, maxAmount);
    }
    
    public int getId() {
        return loot.getId();
    }
    
    public void setId(int id) {
        loot.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", loot.getId());
            put("material", loot.getMaterial().name());
            put("name", loot.getName());
            put("weight", loot.getWeight());
            put("maxAmount", loot.getMaxAmount());
        }};
    }
    
    public Loot toObject() {
        return loot;
    }
}
