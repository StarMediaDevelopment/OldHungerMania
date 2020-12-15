package net.hungermania.maniacore.api.region;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class Region {
    protected int id = -1;
    protected World world;
    protected String worldName;
    protected int xMin, yMin, zMin, xMax, yMax, zMax;
    
    public Region(Location loc1, Location loc2) {
        setBounds(loc1, loc2);
    }

    public Region(int id, String worldName, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        this.id = id;
        this.worldName = worldName;
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;
    }

    public int getXMin() {
        return xMin;
    }

    public int getYMin() {
        return yMin;
    }

    public int getZMin() {
        return zMin;
    }

    public int getXMax() {
        return xMax;
    }

    public int getYMax() {
        return yMax;
    }

    public int getZMax() {
        return zMax;
    }

    public Collection<Location> getLocations() {
        final List<Location> locations = new ArrayList<>();
        for (int x = this.xMin; x <= this.xMax; x++) {
            for (int y = this.yMin; y <= this.yMax; y++) {
                for (int z = this.zMin; z <= this.zMax; z++) {
                    locations.add(new Location(getWorld(), x, y, z));
                }
            }
        }
        return locations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getMinimum() {
        return new Location(getWorld(), this.xMin, this.yMin, this.zMin);
    }

    public Location getMaximum() {
        return new Location(getWorld(), this.xMax, this.yMax, this.zMax);
    }

    public boolean contains(Location loc) {
        if (loc == null) return false;
        int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
        return (loc.getWorld() == getWorld() && x >= this.xMin && x <= this.xMax && y >= this.yMin && y <= this.yMax && z >= this.zMin
                && z <= this.zMax);
    }

    public boolean contains(World world, int x, int y, int z) {
        return (world == getWorld() && x >= this.xMin && x <= this.xMax && y >= this.yMin && y <= this.yMax && z >= this.zMin
                && z <= this.zMax);
    }

    public boolean contains(Player player) {
        return this.contains(player.getLocation());
    }

    public World getWorld() {
        if (this.world == null) {
            this.world = Bukkit.getWorld(this.worldName);
        }
        return world;
    }

    public int getTotalBlockSize() {
        return this.getHeight() * this.getXWidth() * this.getZWidth();
    }

    public int getXWidth() {
        return this.xMax - this.xMin + 1;
    }

    public int getZWidth() {
        return this.zMax - this.zMin + 1;
    }

    public int getHeight() {
        return this.yMax - this.yMin + 1;
    }

    public void setBounds(Location pos1, Location pos2) {
        this.worldName = pos1.getWorld().getName();
        this.xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.yMin = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.yMax = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    }

    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Region cuboid = (Region) o;
        return xMin == cuboid.xMin && yMin == cuboid.yMin && zMin == cuboid.zMin && xMax == cuboid.xMax && yMax == cuboid.yMax && zMax == cuboid.zMax;
    }

    public int hashCode() {
        return Objects.hash(xMin, yMin, zMin, xMax, yMax, zMax);
    }
    
    public void setWorldName(String worldName) {
        this.worldName = worldName;
        this.world = Bukkit.getWorld(worldName);
    }
}
