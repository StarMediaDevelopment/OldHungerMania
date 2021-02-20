package net.hungermania.maniacore.spigot.util;

import lombok.Setter;
import net.hungermania.maniacore.api.ManiaCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class Spawnpoint implements ConfigurationSerializable {
    private String worldName;
    private World world;
    private int x, y, z;
    private float yaw, pitch;
    @Setter private int radius;
    
    public Spawnpoint(Map<String, Object> serialized) {
        this.worldName = (String) serialized.get("world");
        this.x = Integer.parseInt((String) serialized.get("x"));
        this.y = Integer.parseInt((String) serialized.get("y"));
        this.z = Integer.parseInt((String) serialized.get("z"));
        this.yaw = Float.parseFloat((String) serialized.get("yaw"));
        this.pitch = Float.parseFloat((String) serialized.get("pitch"));
        this.radius = Integer.parseInt((String) serialized.get("radius"));
    }

    public Spawnpoint(World world, int x, int y, int z, float yaw, float pitch, int radius) {
        this.world = world;
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.radius = radius;
    }

    public Spawnpoint(Location location) {
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch(), 0);
    }

    public World getWorld() {
        if (world == null) {
            this.world = Bukkit.getWorld(worldName);
        }
        return world;
    }

    public Location getLocation() {
        double x = (this.x + ManiaCore.RANDOM.nextInt(radius)) + .5;
        if (ManiaCore.RANDOM.nextInt(2) < 1) {
            x = x * -1;
        }
        double y = (this.y + ManiaCore.RANDOM.nextInt(radius)) + .5;
        double z = (this.z + ManiaCore.RANDOM.nextInt(radius)) + .5;
        if (ManiaCore.RANDOM.nextInt(2) < 1) {
            z = z * -1;
        }
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("world", getWorld().getName());
            put("x", x + "");
            put("y", y + "");
            put("z", z + "");
            put("yaw", yaw + "");
            put("pitch", pitch + "");
            put("radius", radius + "");
        }};
    }

    public String toString() {
        return "Spawnpoint{" +
                "worldName='" + worldName + '\'' +
                ", world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", radius=" + radius +
                '}';
    }
}
