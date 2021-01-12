package net.hungermania.maniacore.spigot.map;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MapRating {
    private int id;
    private UUID uuid;
    private int mapId;
    private int rating;
    private long lastRated;
}
