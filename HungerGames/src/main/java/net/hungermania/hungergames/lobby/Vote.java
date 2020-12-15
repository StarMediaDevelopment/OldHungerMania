package net.hungermania.hungergames.lobby;

import java.util.Objects;
import java.util.UUID;

public class Vote {
    private int map;
    private UUID uuid;
    private int weight;
    
    public Vote(int map, UUID uuid, int weight) {
        this.map = map;
        this.uuid = uuid;
        this.weight = weight;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public int getWeight() {
        return weight;
    }

    public int getMap() {
        return map;
    }

    public Vote setMap(int map) {
        this.map = map;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(uuid, vote.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
