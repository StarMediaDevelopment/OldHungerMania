package net.hungermania.maniacore.api.nickname;

import net.hungermania.maniacore.api.ranks.Rank;

import java.util.UUID;

public class Nickname {
    private int id;
    private UUID player;
    private String name;
    private UUID skinUUID;
    private boolean active;
    private Rank rank;
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setPlayer(UUID player) {
        this.player = player;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSkinUUID(UUID skinUUID) {
        this.skinUUID = skinUUID;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setRank(Rank rank) {
        this.rank = rank;
    }
    
    public int getId() {
        return id;
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public String getName() {
        return name;
    }
    
    public UUID getSkinUUID() {
        return skinUUID;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public Nickname(UUID player) {
        this.player = player;
    }
    
    public Nickname(int id, UUID player, String name, UUID skinUUID, boolean active, Rank rank) {
        this.id = id;
        this.player = player;
        this.name = name;
        this.skinUUID = skinUUID;
        this.active = active;
        this.rank = rank;
    }
    
    public void setInfo(String name, UUID skinUUID) {
        this.name = name;
        this.skinUUID = skinUUID;
    }
}
