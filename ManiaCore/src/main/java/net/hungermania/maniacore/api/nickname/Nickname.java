package net.hungermania.maniacore.api.nickname;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.ranks.Rank;

import java.util.UUID;

@Getter @AllArgsConstructor
public class Nickname {
    @Setter private int id;
    private UUID player;
    @Setter private String name;
    @Setter private UUID skinUUID;
    @Setter private boolean active;
    @Setter private Rank rank;
    
    public Nickname(UUID player) {
        this.player = player;
    }
    
    public void setInfo(String name, UUID skinUUID) {
        this.name = name;
        this.skinUUID = skinUUID;
    }
}
