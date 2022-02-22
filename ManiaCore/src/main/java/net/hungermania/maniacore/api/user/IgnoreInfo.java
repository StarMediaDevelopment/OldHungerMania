package net.hungermania.maniacore.api.user;

import net.hungermania.manialib.data.model.IRecord;

import java.util.Objects;
import java.util.UUID;

public class IgnoreInfo implements IRecord {
    
    private int id;
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
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public void setPlayer(UUID player) {
        this.player = player;
    }
    
    public UUID getIgnored() {
        return ignored;
    }
    
    public void setIgnored(UUID ignored) {
        this.ignored = ignored;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getIgnoredName() {
        return ignoredName;
    }
    
    public void setIgnoredName(String ignoredName) {
        this.ignoredName = ignoredName;
    }
    
    public int hashCode() {
        return Objects.hash(player, ignored);
    }
}
