package net.hungermania.maniacore.api.records;

import net.hungermania.manialib.sql.*;
import net.hungermania.maniacore.api.skin.Skin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinRecord implements IRecord<Skin> {
    
    private Skin skin;
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "skins");
        table.addColumn(new Column("id", DataType.INT, true, true));
        table.addColumn(new Column("name", DataType.VARCHAR, 200));
        table.addColumn(new Column("uuid", DataType.VARCHAR, 48));
        table.addColumn(new Column("value", DataType.VARCHAR, 1000));
        table.addColumn(new Column("signature", DataType.VARCHAR, 1000));
        return table;
    }
    
    public SkinRecord(Skin skin) {
        this.skin = skin;
    }
    
    public SkinRecord(Row row) {
        int id = row.getInt("id");
        String name = row.getString("name");
        UUID uuid = UUID.fromString(row.getString("uuid"));
        String value = row.getString("value");
        String signature = row.getString("signature");
        this.skin = new Skin(id, uuid, name, value, signature);
    }
    
    @Override
    public int getId() {
        return skin.getId();
    }
    
    @Override
    public void setId(int id) {
        skin.setId(id);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", skin.getId());
            put("name", skin.getName());
            put("uuid", skin.getUuid().toString());
            put("value", skin.getValue());
            put("signature", skin.getSignature());
        }};
    }
    
    @Override
    public Skin toObject() {
        return skin;
    }
}
