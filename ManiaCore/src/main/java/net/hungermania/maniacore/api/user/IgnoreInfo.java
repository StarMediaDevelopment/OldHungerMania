package net.hungermania.maniacore.api.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class IgnoreInfo {
    
    @Setter private int id;
    private UUID player, ignored;
    private long timestamp;
    private String ignoredName;
    
    public IgnoreInfo(int id, UUID player, UUID ignored, long timestamp, String ignoredName) {
        this.id = id;
        this.player = player;
        this.ignored = ignored;
        this.timestamp = timestamp;
        this.ignoredName = ignoredName;
    }
    
    public IgnoreInfo(UUID player, UUID ignored, long timestamp, String ignoredName) {
        this.player = player;
        this.ignored = ignored;
        this.timestamp = timestamp;
        this.ignoredName = ignoredName;
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        IgnoreInfo that = (IgnoreInfo) o;
        return Objects.equals(player, that.player) && Objects.equals(ignored, that.ignored);
    }
    
    public int hashCode() {
        return Objects.hash(player, ignored);
    }
}
