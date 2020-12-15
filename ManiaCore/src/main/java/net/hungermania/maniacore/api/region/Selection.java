package net.hungermania.maniacore.api.region;

import org.bukkit.Location;
import org.bukkit.World;

public class Selection {
    
    private World world;
    private Location pointA, pointB;
    
    public Selection(Location pointA, Location pointB) {
        if (!pointA.getWorld().getName().equalsIgnoreCase(pointB.getWorld().getName())) {
            return;
        }
        
        this.world = pointA.getWorld();
        this.pointA = pointA;
        this.pointB = pointB;
    }
    
    public Selection() {}
    
    public Location getPointA() {
        return pointA;
    }
    
    public void setPointA(Location pointA) {
        if (pointB != null) {
            if (!pointB.getWorld().getName().equalsIgnoreCase(pointA.getWorld().getName())) {
                return;
            }
        }
        
        this.pointA = pointA;
        if (world == null) this.world = pointA.getWorld();
    }
    
    public Location getPointB() {
        return pointB;
    }
    
    public void setPointB(Location pointB) {
        if (pointA != null) {
            if (!pointA.getWorld().getName().equalsIgnoreCase(pointB.getWorld().getName())) {
                return;
            }
        }
        
        this.pointB = pointB;
        if (world == null) this.world = pointB.getWorld();
    }
    
    public World getWorld() {
        return world;
    }
    
    public boolean hasMinimum() {
        return pointA != null;
    }
    
    public boolean hasMaximum() {
        return pointB != null;
    }
}