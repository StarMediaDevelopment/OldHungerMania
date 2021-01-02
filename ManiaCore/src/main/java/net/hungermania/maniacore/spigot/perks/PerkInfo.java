package net.hungermania.maniacore.spigot.perks;

import lombok.*;

import java.util.*;

@Getter
public class PerkInfo {
    @Setter private int id;
    private UUID uuid;
    private String name;
    @Getter(AccessLevel.NONE) private boolean value;
    @Setter private Set<Integer> unlockedTiers = new HashSet<>();
    private long created;
    @Setter private long modified;
    @Setter private boolean active;
    
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
    
    public void setValue(boolean value) {
        this.value = value;
        this.modified = System.currentTimeMillis();
    }
    
    public boolean getValue() {
        return value;
    }
}
