package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.manialib.sql.*;

import java.util.*;

public class StatRecord implements IRecord<Statistic> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "stats");
        table.addColumn(new Column("id", DataType.INT, true, true));
        table.addColumn(new Column("uuid", DataType.VARCHAR, 36));
        table.addColumn(new Column("name", DataType.VARCHAR, 100));
        table.addColumn(new Column("value", DataType.VARCHAR, 1000));
        table.addColumn(new Column("created", DataType.BIGINT));
        table.addColumn(new Column("modified", DataType.BIGINT));
        return table;
    }
    
    private Statistic statistic;
    
    public StatRecord(Statistic statistic) {
        this.statistic = statistic;
    }
    
    public StatRecord(Row row) {
        int id = row.getInt("id");
        UUID uuid = UUID.fromString(row.getString("uuid"));
        String name = row.getString("name");
        long created = row.getLong("created");
        long modified = row.getLong("modified");
        String value = row.getString("value");
        this.statistic = new Statistic(id, uuid, name, value, created, modified);
    }
    
    public int getId() {
        return statistic.getId();
    }
    
    public void setId(int id) {
        statistic.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", statistic.getId());
            put("uuid", statistic.getUuid().toString());
            put("value", statistic.getValue() + "");
            put("created", statistic.getCreated());
            put("modified", statistic.getModified());
            put("name", statistic.getName());
        }};
    }
    
    public Statistic toObject() {
        return statistic;
    }
}
