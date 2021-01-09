package net.hungermania.maniacore.api.friends;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class FriendRequest {
    
    @Setter private int id;
    private UUID sender;
    private UUID to;
    private long timestamp;
    
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
