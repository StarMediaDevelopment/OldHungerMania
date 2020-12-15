package net.hungermania.maniacore.api.friends;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter @Builder @AllArgsConstructor
public class FriendNotification {
    
    public enum Type {
        ACCEPTED, DENIED, REMOVED
    }
    
    @Setter private int id;
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
}
