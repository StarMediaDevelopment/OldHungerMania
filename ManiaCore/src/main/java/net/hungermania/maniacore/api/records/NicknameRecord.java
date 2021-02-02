package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.nickname.Nickname;
import net.hungermania.manialib.sql.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameRecord implements IRecord<Nickname> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "nicknames");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("player", DataType.VARCHAR, 36);
        table.addColumn("name", DataType.VARCHAR, 16);
        table.addColumn("skinUUID", DataType.VARCHAR, 36);
        table.addColumn("active", DataType.VARCHAR, 5);
        return null;
    }
    
    private Nickname object;
    
    public NicknameRecord(Nickname nickname) {
        this.object = nickname;
    }
    
    public NicknameRecord(Row row) {
        int id = row.getInt("id");
        UUID player = row.getUUID("player");
        String name = row.getString("name");
        UUID skinUUID = row.getUUID("skinUUID");
        boolean active = row.getBoolean("active");
        this.object = new Nickname(id, player, name, skinUUID, active);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", object.getId());
            put("player", object.getPlayer().toString());
            put("name", object.getName());
            put("skinUUID", object.getSkinUUID().toString());
            put("active", object.isActive());
        }};
    }
    
    public Nickname toObject() {
        return object;
    }
    
    public void setId(int id) {
        object.setId(id);
    }
    
    public int getId() {
        return object.getId();
    }
}
