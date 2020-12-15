package net.hungermania.manialib.multicraft.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder @Getter @Setter
public class PlayerStatus {
    private String ip;
    private int id;
    private String name;
}