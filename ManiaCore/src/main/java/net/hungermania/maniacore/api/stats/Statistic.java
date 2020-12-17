package net.hungermania.maniacore.api.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Statistic {
    
    @Setter private int id;
    private UUID uuid;
    private String name;
    private String value;
    private long created;
    @Setter private long modified;
    
    public Statistic(int id, UUID uuid, String name, String value, long created, long modified) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.value = value;
        this.created = created;
        this.modified = modified;
    }
    
    public Statistic(UUID uuid, String name, String value, long created, long modified) {
        this.uuid = uuid;
        this.name = name;
        this.value = value;
        this.created = created;
        this.modified = modified;
    }
    
    public String getValueAsString() {
        return this.value;
    }
    
    public int getValueAsInt() {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
    
    public void increment() {
        try {
            int val = Integer.parseInt(this.value);
            val++;
            this.value = val + "";
            this.modified = System.currentTimeMillis();
        } catch (Exception e) {}
    }
    
    public void setValue(String value) {
        this.value = value;
        this.modified = System.currentTimeMillis();
    }
    
    public void setValue(int value) {
        this.value = String.valueOf(value);
        this.modified = System.currentTimeMillis();
    }
}
