package net.hungermania.maniacore.api.ranks;

import java.util.UUID;

public class RankInfo {
    private final UUID user;
    private String actor = "";
    private long expire = -1;
    private Rank rank = Rank.DEFAULT, previousRank = Rank.DEFAULT;
    
    public RankInfo(UUID user) {
        this.user = user;
    }
    
    public void setActor(String actor) {
        this.actor = actor;
    }
    
    public void setExpire(long expire) {
        this.expire = expire;
    }
    
    public void setRank(Rank rank) {
        this.rank = rank;
    }
    
    public void setPreviousRank(Rank previousRank) {
        this.previousRank = previousRank;
    }
    
    public UUID getUser() {
        return user;
    }
    
    public String getActor() {
        return actor;
    }
    
    public long getExpire() {
        return expire;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public Rank getPreviousRank() {
        return previousRank;
    }
    
    public boolean isExpired() {
        if (expire == -1) return false;
        else return System.currentTimeMillis() > expire;
    }
}