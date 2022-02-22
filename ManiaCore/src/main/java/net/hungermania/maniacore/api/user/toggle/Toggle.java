package net.hungermania.maniacore.api.user.toggle;

import net.hungermania.manialib.data.model.IRecord;

import java.util.UUID;

public class Toggle implements IRecord {
    
    private int id; //Database purposes
    private UUID uuid;
    private String name, value, defaultValue = "";
    
    public Toggle(UUID uuid, String name, String value, String defaultValue) {
        this.uuid = uuid;
        this.name = name;
        this.value = value;
        this.defaultValue = defaultValue;
    }
    
    public Toggle(int id, UUID uuid, String name, String value) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.value = value;
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
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public boolean getAsBoolean() {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {}
        return false;
    }
}