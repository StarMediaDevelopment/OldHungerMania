package net.hungermania.hungergames.newsettings;

import net.hungermania.manialib.util.Unit;


public class GameSetting implements Cloneable {
    
    private int id;
    private String groupName, settingName;
    private String value;
    private Type type;
    private Unit unit;
    public GameSetting(int id, String groupName, String settingName, String value, Type type, Unit unit) {
        this.id = id;
        this.groupName = groupName;
        this.settingName = settingName;
        this.value = value;
        this.type = type;
        this.unit = unit;
    }
    
    public GameSetting(String groupName, String settingName, String value, Type type) {
        this(-1, groupName, settingName, value, type, Unit.UNDEFINED);
    }
    
    public GameSetting(String groupName, String settingName, String value, Type type, Unit unit) {
        this(-1, groupName, settingName, value, type, unit);
    }
    
    public GameSetting(String groupName, String settingName, String value, Unit unit) {
        this(-1, groupName, settingName, value, Type.NUMBER, unit);
    }
    
    public String getValue() {
        return value;
    }
    
    public boolean getAsBoolean() {
        if (this.type == Type.BOOLEAN) {
            try {
                return Boolean.parseBoolean(value);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        
        return false;
    }
    
    public int getAsInt() {
        if (this.type == Type.NUMBER) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        return 0;
    }
    
    public double getAsDouble() {
        if (this.type == Type.NUMBER) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        return 0;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public String getSettingName() {
        return settingName;
    }
    
    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public GameSetting clone() {
        return new GameSetting(getId(), getGroupName(), getSettingName(), getValue(), getType(), getUnit());
    }
    
    public enum Type {
        NUMBER, BOOLEAN, OTHER, TIME
    }
}
