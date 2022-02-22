package net.hungermania.hungergames.newsettings;

import net.hungermania.hungergames.newsettings.enums.Setting;

import java.util.Map;


public class SettingGroup {
    private String name;
    private Map<Setting, GameSetting> settings;
    
    public SettingGroup(String name, Map<Setting, GameSetting> settings) {
        this.name = name;
        this.settings = settings;
    }
    
    public GameSetting getSetting(Setting setting) {
        return settings.get(setting);
    }
    
    public String getName() {
        return name;
    }
    
    public Map<Setting, GameSetting> getSettings() {
        return settings;
    }
}
