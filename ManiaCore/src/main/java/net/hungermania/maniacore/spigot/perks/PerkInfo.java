package net.hungermania.maniacore.spigot.perks;

import net.hungermania.manialib.data.model.IRecord;

import java.util.*;

public class PerkInfo implements IRecord {
    private int id;
    private UUID uuid;
    private String name;
    private boolean value;
    private Set<Integer> unlockedTiers = new HashSet<>();
    private long created;
    private long modified;
    private boolean active;
    
    public PerkInfo(int id, UUID uuid, String name, boolean value, Set<Integer> unlockedTiers, long created, long modified, boolean active) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.value = value;
        this.unlockedTiers = unlockedTiers;
        this.created = created;
        this.modified = modified;
        this.active = active;
    }
    
    public PerkInfo(UUID uuid, String name, boolean value, long created, long modified) {
        this.uuid = uuid;
        this.name = name;
        this.value = value;
        this.created = created;
        this.modified = modified;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isValue() {
        return value;
    }
    
    public Set<Integer> getUnlockedTiers() {
        return unlockedTiers;
    }
    
    public void setUnlockedTiers(Set<Integer> unlockedTiers) {
        this.unlockedTiers = unlockedTiers;
    }
    
    public long getCreated() {
        return created;
    }
    
    public void setCreated(long created) {
        this.created = created;
    }
    
    public long getModified() {
        return modified;
    }
    
    public void setModified(long modified) {
        this.modified = modified;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean getValue() {
        return value;
    }
    
    public void setValue(boolean value) {
        this.value = value;
        this.modified = System.currentTimeMillis();
    }
}
