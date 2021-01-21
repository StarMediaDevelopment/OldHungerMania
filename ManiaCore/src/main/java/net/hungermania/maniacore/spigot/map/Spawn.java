package net.hungermania.maniacore.spigot.map;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.util.Position;
import net.hungermania.manialib.data.annotations.TableInfo;
import net.hungermania.manialib.data.model.IRecord;

@Getter
@Setter
@TableInfo(tableName = "mapspawns")
public class Spawn extends Position implements IRecord {
    private int id;
    private int number;
    private int mapId;

    protected Spawn() {
    }

    public Spawn(int x, int y, int z, float yaw, float pitch, int number, int mapId) {
        super(x, y, z, yaw, pitch);
        this.number = number;
        this.mapId = mapId;
    }

    public Spawn(int number, int mapId, int x, int y, int z) {
        super(x, y, z);
    }
}
