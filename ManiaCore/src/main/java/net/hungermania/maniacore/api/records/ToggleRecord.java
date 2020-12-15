package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.user.toggle.Toggle;
import net.hungermania.manialib.sql.*;

import java.util.*;

public class ToggleRecord implements IRecord<Toggle> {
    
    /*
    private int id; //Database purposes
    private UUID uuid;
    private String name, value, defaultValue;
     */
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "toggles");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("uuid", DataType.VARCHAR, 36);
        table.addColumn("name", DataType.VARCHAR, 50);
        table.addColumn("value", DataType.VARCHAR, 100);
        return table;
    }
    
    private Toggle object;
    
    public ToggleRecord(Toggle object) {
        this.object = object;
    }
    
    public ToggleRecord(Row row) {
        int id = row.getInt("id");
        UUID uuid = row.getUUID("uuid");
        String name = row.getString("name");
        String value = row.getString("value");
        this.object = new Toggle(id, uuid, name, value);
    }
    
    public int getId() {
        return object.getId();
    }
    
    public void setId(int id) {
        object.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", object.getId());
            put("uuid", object.getUuid().toString());
            put("name", object.getName());
            put("value", object.getValue());
        }};
    }
    
    public Toggle toObject() {
        return object;
    }
}