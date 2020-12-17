package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.manialib.sql.*;

import java.util.*;

public class UserRecord implements IRecord<User> {
    
    private User user;
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "users");
        Column id = new Column("id", DataType.INT, true, true);
        Column uniqueId = new Column("uniqueId", DataType.VARCHAR, 64, false, false);
        Column name = new Column("name", DataType.VARCHAR, 32, false, false);
        Column rank = new Column("rank", DataType.VARCHAR, 100);
        Column channel = new Column("channel", DataType.VARCHAR, 15);
        table.addColumns(id, uniqueId, name, rank, channel);
        return table;
    }
    
    public UserRecord(User user) {
        this.user = user;
    }
    
    public UserRecord(Row row) {
        int id = row.getInt("id");
        UUID uniqueId = UUID.fromString(row.getString("uniqueId"));
        String name = row.getString("name");
        Rank rank = Rank.valueOf(row.getString("rank"));
        
        Channel channel;
        try {
            channel = Channel.valueOf(row.getString("channel"));
        } catch (Exception e) {
            channel = Channel.GLOBAL;
        }
        this.user = new User(id, uniqueId, name, rank, channel);
    }
    
    public int getId() {
        return user.getId();
    }
    
    public void setId(int id) {
        user.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", user.getId());
            put("uniqueId", user.getUniqueId().toString());
            put("name", user.getName());
            put("rank", user.getRank().name());
            put("channel", user.getChannel().name());
        }};
    }
    
    public User toObject() {
        return user;
    }
}
