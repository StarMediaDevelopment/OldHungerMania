package net.hungermania.maniacore.spigot.map;

import java.util.UUID;

public class MapRating {
    private int id;
    private UUID uuid;
    private int mapId;
    private int rating;
    private long lastRated;
    
    public MapRating(int id, UUID uuid, int mapId, int rating, long lastRated) {
        this.id = id;
        this.uuid = uuid;
        this.mapId = mapId;
        this.rating = rating;
        this.lastRated = lastRated;
    }
    
    public int getId() {
        return id;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public int getMapId() {
        return mapId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public long getLastRated() {
        return lastRated;
    }
}
