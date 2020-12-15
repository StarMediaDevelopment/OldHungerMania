package net.hungermania.maniacore.api.user.toggle;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class Toggle {
    
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
    
    public boolean getAsBoolean() {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {}
        return false;
    }
}