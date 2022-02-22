package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.friends.FriendRequest;
import net.hungermania.manialib.sql.*;

import java.util.HashMap;
import java.util.Map;

public class FriendRequestRecord implements IRecord<FriendRequest> {
    
    public FriendRequestRecord(Row row) {
        this.object = new FriendRequest(row.getInt("id"), row.getUUID("from"), row.getUUID("to"), row.getLong("timestamp"));
    }
    
    private FriendRequest object;
    
    public FriendRequestRecord(FriendRequest object) {
        this.object = object;
    }
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "friendrequests");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("sender", DataType.VARCHAR, 36);
        table.addColumn("to", DataType.VARCHAR, 36);
        table.addColumn("timestamp", DataType.BIGINT);
        return table;
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
            put("sender", object.getSender().toString());
            put("to", object.getTo().toString());
            put("timestamp", object.getTimestamp());
        }};
    }
    
    public FriendRequest toObject() {
        return object;
    }
}