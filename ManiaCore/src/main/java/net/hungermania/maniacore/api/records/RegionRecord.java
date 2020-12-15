package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.region.Region;
import net.hungermania.manialib.sql.*;

import java.util.HashMap;
import java.util.Map;

public class RegionRecord implements IRecord<Region> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "region");
        Column id = new Column("id", DataType.INT, true, true);
        Column world = new Column("world", DataType.VARCHAR, 30, false, false);
        Column xMin = new Column("xMin", DataType.INT, false, false);
        Column yMin = new Column("yMin", DataType.INT, false, false);
        Column zMin = new Column("zMin", DataType.INT, false, false);
        Column xMax = new Column("xMax", DataType.INT, false, false);
        Column yMax = new Column("yMax", DataType.INT, false, false);
        Column zMax = new Column("zMax", DataType.INT, false, false);
        
        table.addColumns(id, world, xMin, yMin, zMin, xMax, yMax, zMax);
        return table;
    }
    
    private Region region;

    public RegionRecord(Region region) {
        this.region = region;
    }
    
    public RegionRecord(Row row) {
        int id = row.getInt("id");
        String world = row.getString("world");
        int xMin = row.getInt("xMin");
        int yMin = row.getInt("yMin");
        int zMin = row.getInt("zMin");
        int xMax = row.getInt("xMax");
        int yMax = row.getInt("yMax");
        int zMax = row.getInt("zMax");
        this.region = new Region(id, world, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public int getId() {
        return region.getId();
    }

    public void setId(int id) {
        region.setId(id);
    }

    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", region.getId());
            put("world", region.getWorld().getName());
            put("xMin", region.getXMin());
            put("yMin", region.getYMin());
            put("zMin", region.getZMin());
            put("xMax", region.getXMax());
            put("yMax", region.getYMax());
            put("zMax", region.getZMax());
        }};
    }

    public Region toObject() {
        return region;
    }
}
