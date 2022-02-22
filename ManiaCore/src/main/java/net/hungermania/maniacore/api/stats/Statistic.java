package net.hungermania.maniacore.api.stats;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.manialib.data.model.IRecord;

import java.util.UUID;

public class Statistic implements IRecord {
    
    private int id;
    private UUID uuid;
    private String name;
    private String value;
    private long created;
    private long modified;
    
    private User user;
    
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
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setCreated(long created) {
        this.created = created;
    }
    
    public void setModified(long modified) {
        this.modified = modified;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    public long getCreated() {
        return created;
    }
    
    public long getModified() {
        return modified;
    }
    
    public User getUser() {
        if (user == null) {
            this.user = ManiaCore.getInstance().getUserManager().getUser(uuid);
        }
        return user;
    }

    public String getAsString() {
        return this.value;
    }

    public int getAsInt() {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean getAsBoolean() {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return false;
        }
    }

    public void increment() {
        try {
            int val = Integer.parseInt(this.value);
            val++;
            this.value = val + "";
            this.modified = System.currentTimeMillis();
        } catch (Exception e) {
        }
    }

    public void setValue(String value) {
        this.value = value;
        this.modified = System.currentTimeMillis();
    }

    public void setValue(boolean value) {
        this.value = value + "";
        this.modified = System.currentTimeMillis();
    }

    public void setValue(int value) {
        this.value = String.valueOf(value);
        this.modified = System.currentTimeMillis();
    }
}
