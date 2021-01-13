package net.hungermania.maniacore.api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class Position {
    private int x, y, z;
    private float yaw, pitch;
    
    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
