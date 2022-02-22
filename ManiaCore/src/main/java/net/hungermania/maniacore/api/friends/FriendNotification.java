package net.hungermania.maniacore.api.friends;

import net.hungermania.manialib.data.model.IRecord;

import java.util.Map;
import java.util.UUID;

public class FriendNotification implements IRecord {
    
    public enum Type {
        ACCEPTED, DENIED, REMOVED
    }
    
    private int id;
    private Type type;
    private UUID sender, target;
    private long timestamp;
    
    public FriendNotification(Type type, UUID sender, UUID target, long timestamp) {
        this.type = type;
        this.sender = sender;
        this.target = target;
        this.timestamp = timestamp;
    }
    
    public FriendNotification(Map<String, String> jedisData) {
        this.id = Integer.parseInt(jedisData.get("id"));
        this.type = Type.valueOf(jedisData.get("type"));
        this.sender = UUID.fromString(jedisData.get("sender"));
        this.target = UUID.fromString(jedisData.get("target"));
        this.timestamp = Long.parseLong(jedisData.get("timestamp"));
    }
    
    public FriendNotification(int id, Type type, UUID sender, UUID target, long timestamp) {
        this.id = id;
        this.type = type;
        this.sender = sender;
        this.target = target;
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
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public UUID getSender() {
        return sender;
    }
    
    public void setSender(UUID sender) {
        this.sender = sender;
    }
    
    public UUID getTarget() {
        return target;
    }
    
    public void setTarget(UUID target) {
        this.target = target;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
