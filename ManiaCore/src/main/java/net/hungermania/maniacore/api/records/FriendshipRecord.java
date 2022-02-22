package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.friends.Friendship;
import net.hungermania.manialib.sql.*;

import java.util.*;

public class FriendshipRecord implements IRecord<Friendship> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "friendships");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("player1", DataType.VARCHAR, 36);
        table.addColumn("player2", DataType.VARCHAR, 36);
        table.addColumn("timestamp", DataType.BIGINT);
        return table;
    }
    
    private Friendship friendship;
    
    public FriendshipRecord(Friendship friendship) {
        this.friendship = friendship;
    }
    
    public FriendshipRecord(Row row) {
        this.friendship = new Friendship(row.getInt("id"), row.getUUID("player1"), row.getUUID("player2"), row.getLong("timestamp"));
    }
    
    public int getId() {
        return friendship.getId();
    }
    
    public void setId(int id) {
        friendship.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", friendship.getId());
            put("player1", friendship.getPlayer1().toString());
            put("player2", friendship.getPlayer2().toString());
            put("timestamp", friendship.getTimestamp());
        }};
    }
    
    public Friendship toObject() {
        return friendship;
    }
}
