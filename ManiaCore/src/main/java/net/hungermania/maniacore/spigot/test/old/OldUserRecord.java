package net.hungermania.maniacore.spigot.test.old;

import net.hungermania.manialib.sql.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OldUserRecord implements IRecord<OldTestUser> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "testolduser");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("uuid", DataType.VARCHAR, 36);
        table.addColumn("name", DataType.VARCHAR, 16);
        table.addColumn("incognito", DataType.VARCHAR, 5);
        table.addColumn("lastLogin", DataType.BIGINT);
        table.addColumn("coins", DataType.DOUBLE);
        return table;
    }
    
    private OldTestUser object;

    public OldUserRecord(OldTestUser object) {
        this.object = object;
    }
    
    public OldUserRecord(Row row) {
        int id = row.getInt("id");
        UUID uuid = row.getUUID("UUID");
        String name = row.getString("name");
        boolean incognito = row.getBoolean("incognito");
        long lastLogin = row.getLong("lastLogin");
        double coins = row.getDouble("coins");
        this.object = new OldTestUser(id, uuid, name, incognito, lastLogin, coins);
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
            put("incognito", object.isIncognito() + "");
            put("lastLogin", object.getLastLogin());
            put("coins", object.getCoins());
        }};
    }

    public OldTestUser toObject() {
        return object;
    }
}
