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
        Column networkExperience = new Column("networkExperience", DataType.BIGINT, false, false);
        Column coins = new Column("coins", DataType.INT);
        Column rank = new Column("rank", DataType.VARCHAR, 100);
        Column channel = new Column("channel", DataType.VARCHAR, 15);
        Column onlineTime = new Column("onlineTime", DataType.BIGINT);
        table.addColumns(id, uniqueId, name, networkExperience, coins, rank, channel, onlineTime);
        return table;
    }
    
    public UserRecord(User user) {
        this.user = user;
    }
    
    public UserRecord(Row row) {
        int id = row.getInt("id");
        UUID uniqueId = UUID.fromString(row.getString("uniqueId"));
        String name = row.getString("name");
        long networkExperience = row.getLong("networkExperience");
        int coins = row.getInt("coins");
        Rank rank = Rank.valueOf(row.getString("rank"));
        
        Channel channel;
        try {
            channel = Channel.valueOf(row.getString("channel"));
        } catch (Exception e) {
            channel = Channel.GLOBAL;
        }
        long onlineTime = row.getLong("onlineTime");
        this.user = new User(id, uniqueId, name, networkExperience, coins, rank, channel, onlineTime);
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
            put("networkExperience", user.getNetworkExperience());
            put("coins", user.getCoins());
            put("rank", user.getRank().name());
            put("channel", user.getChannel().name());
            put("onlineTime", user.getOnlineTime());
        }};
    }
    
    public User toObject() {
        return user;
    }
}
