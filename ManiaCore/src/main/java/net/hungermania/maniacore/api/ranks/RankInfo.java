package net.hungermania.maniacore.api.ranks;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class RankInfo {
    private final UUID user;
    private String actor = "";
    private long expire = -1;
    private Rank rank = Rank.DEFAULT, previousRank = Rank.DEFAULT;
    
    public RankInfo(UUID user) {
        this.user = user;
    }
    
    public boolean isExpired() {
        if (expire == -1) return false;
        else return System.currentTimeMillis() > expire;
    }
}