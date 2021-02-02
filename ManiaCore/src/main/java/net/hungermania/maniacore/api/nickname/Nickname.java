package net.hungermania.maniacore.api.nickname;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @AllArgsConstructor
public class Nickname {
    @Setter private int id;
    private UUID player;
    @Setter private String name;
    @Setter private UUID skinUUID;
    @Setter private boolean active;
    
    public Nickname(UUID player) {
        this.player = player;
    }
    
    public void setInfo(String name, UUID skinUUID) {
        this.name = name;
        this.skinUUID = skinUUID;
    }
}
