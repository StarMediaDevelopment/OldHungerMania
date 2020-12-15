package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.friends.FriendRequest;
import net.hungermania.manialib.sql.*;

import java.util.HashMap;
import java.util.Map;

public class FriendRequestRecord implements IRecord<FriendRequest> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "friendrequests");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("from", DataType.VARCHAR, 36);
        table.addColumn("to", DataType.VARCHAR, 36);
        table.addColumn("timestamp", DataType.BIGINT);
        return table;
    }
    
    private FriendRequest object;
    
    public FriendRequestRecord(FriendRequest object) {
        this.object = object;
    }
    
    public FriendRequestRecord(Row row) {
        this.object = FriendRequest.builder().id(row.getInt("id")).from(row.getUUID("from")).to(row.getUUID("to")).timestamp(row.getLong("timestamp")).build();
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
            put("from", object.getFrom().toString());
            put("to", object.getTo().toString());
            put("timestamp", object.getTimestamp());
        }};
    }
    
    public FriendRequest toObject() {
        return object;
    }
}