package net.hungermania.hungergames.newsettings;

import lombok.Getter;
import net.hungermania.hungergames.newsettings.enums.Setting;

import java.util.Map;

@Getter
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
}
