package net.hungermania.maniacore.api.friends;

import net.hungermania.manialib.data.model.IRecord;

import java.util.Map;
import java.util.UUID;

public class FriendRequest implements IRecord {
    
    private int id;
    private UUID sender;
    private UUID to;
    private long timestamp;
    
    public FriendRequest(int id, UUID sender, UUID to, long timestamp) {
        this.id = id;
        this.sender = sender;
        this.to = to;
        this.timestamp = timestamp;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    public UUID getSender() {
        return sender;
    }
    
    public void setSender(UUID sender) {
        this.sender = sender;
    }
    
    public UUID getTo() {
        return to;
    }
    
    public void setTo(UUID to) {
        this.to = to;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public FriendRequest(UUID sender, UUID to, long timestamp) {
        this.sender = sender;
        this.to = to;
        this.timestamp = timestamp;
    }
    
    public FriendRequest(Map<String, String> jedisData) {
        this.id = Integer.parseInt(jedisData.get("id"));
        this.sender = UUID.fromString(jedisData.get("from"));
        this.to = UUID.fromString(jedisData.get("to"));
        this.timestamp = Long.parseLong(jedisData.get("timestamp"));
    }
}
