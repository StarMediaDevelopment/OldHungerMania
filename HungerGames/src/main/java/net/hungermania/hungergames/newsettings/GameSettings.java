package net.hungermania.hungergames.newsettings;

import net.hungermania.hungergames.lobby.GameOverrides;
import net.hungermania.hungergames.map.HGMap;
import net.hungermania.hungergames.newsettings.enums.Setting;

import java.util.Map;

//Represents settings in the current game
public class GameSettings {
    private Map<Setting, GameSetting> settings;
     private HGMap map;

    public GameSettings(HGMap map, Map<Setting, GameSetting> s, GameOverrides overrides) {
        this.map = map;
        s.forEach((setting, gameSetting) -> settings.put(setting, gameSetting.clone())); //This copies the settings so that modifications to this do not affect other games
        //TODO Overrides
    }

    public GameSetting getSetting(Setting setting) {
        return settings.get(setting);
    }
    
    public Map<Setting, GameSetting> getSettings() {
        return settings;
    }
    
    public void setSettings(Map<Setting, GameSetting> settings) {
        this.settings = settings;
    }
    
    public HGMap getMap() {
        return map;
    }
    
    public void setMap(HGMap map) {
        this.map = map;
    }
}