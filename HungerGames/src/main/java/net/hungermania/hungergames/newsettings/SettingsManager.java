package net.hungermania.hungergames.newsettings;

import net.hungermania.hungergames.newsettings.enums.Setting;
import net.hungermania.hungergames.records.GameSettingRecord;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.manialib.collection.ListMap;
import net.hungermania.manialib.sql.IRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SettingsManager {

     private Map<String, SettingGroup> settingsGroup = new HashMap<>();

    public void loadData() {
        List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(GameSettingRecord.class, null, null);
        ListMap<String, GameSetting> settings = new ListMap<>();
        if (records.isEmpty()) {
            for (Setting value : Setting.values()) {
                GameSetting setting = new GameSetting("default", value.name(), value.getDefaultValue(), value.getType(), value.getUnit());
                ManiaCore.getInstance().getDatabase().addRecordToQueue(new GameSettingRecord(setting));
                settings.add("default", setting);
            }
            ManiaCore.getInstance().getDatabase().pushQueue();
        } else {
            for (IRecord record : records) {
                GameSetting setting = ((GameSettingRecord) record).toObject();
                settings.add(setting.getGroupName(), setting);
            }
        }

        for (Setting setting : Setting.values()) {
            for (String key : settings.keySet()) {
                boolean containsSetting = false;
                for (GameSetting gameSetting : settings.get(key)) {
                    if (gameSetting.getSettingName().equalsIgnoreCase(setting.name())) {
                        containsSetting = true;
                        break;
                    }
                }

                if (containsSetting)
                    continue;
                GameSetting s = new GameSetting("default", setting.name(), setting.getDefaultValue(), setting.getType(), setting.getUnit());
                ManiaCore.getInstance().getDatabase().addRecordToQueue(new GameSettingRecord(s));
                settings.add(key, s);
            }
        }

        for (Entry<String, List<GameSetting>> entry : settings.entrySet()) {
            Map<Setting, GameSetting> groupSettings = new HashMap<>();
            for (GameSetting gameSetting : entry.getValue()) {
                Setting setting = Setting.valueOf(gameSetting.getSettingName());
                groupSettings.put(setting, gameSetting);
            }
            this.settingsGroup.put(entry.getKey(), new SettingGroup(entry.getKey(), groupSettings));
        }
    }
    
    public Map<String, SettingGroup> getSettingsGroup() {
        return settingsGroup;
    }
    
    public void setSettingsGroup(Map<String, SettingGroup> settingsGroup) {
        this.settingsGroup = settingsGroup;
    }
}
