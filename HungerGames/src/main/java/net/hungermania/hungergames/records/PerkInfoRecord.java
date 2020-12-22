package net.hungermania.hungergames.records;

import net.hungermania.hungergames.perks.PerkInfo;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.manialib.sql.*;

import java.util.*;

public class PerkInfoRecord implements IRecord<PerkInfo> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "perks");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("uuid", DataType.VARCHAR, 36);
        table.addColumn("name", DataType.VARCHAR, 64);
        table.addColumn("value", DataType.VARCHAR, 5);
        table.addColumn("created", DataType.BIGINT);
        table.addColumn("modified", DataType.BIGINT);
        table.addColumn("unlockedTiers", DataType.VARCHAR, 1000);
        table.addColumn("active", DataType.VARCHAR, 5);
        return table;
    }
    
    private PerkInfo object;
    
    public PerkInfoRecord(PerkInfo object) {
        this.object = object;
    }
    
    public PerkInfoRecord(Row row) {
        int id = row.getInt("id");
        UUID uuid = UUID.fromString(row.getString("uuid"));
        String name = row.getString("name");
        boolean value = row.getBoolean("value");
        long created = row.getLong("created");
        long modified = row.getLong("modified");
        String rawUn = row.getString("unlockedTiers");
        Set<Integer> unlockedTiers = new HashSet<>();
        for (String s : rawUn.split(",")) {
            try {
                unlockedTiers.add(Integer.parseInt(s));
            } catch (Exception e) {}
        }
        boolean active = row.getBoolean("active");
        this.object = new PerkInfo(id, uuid, name, value, unlockedTiers, created, modified, active);
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
            put("created", object.getCreated());
            put("modified", object.getModified());
            put("unlockedTiers", Utils.join(object.getUnlockedTiers(), ","));
            put("active", object.isActive());
        }};
    }
    
    public PerkInfo toObject() {
        return object;
    }
}