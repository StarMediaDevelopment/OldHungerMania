package net.hungermania.maniacore.api.leveling;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.LevelRecord;
import net.hungermania.manialib.sql.IRecord;
import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LevelManager {
    private final ManiaCore maniaCore;
    private Map<Integer, Level> levels = new TreeMap<>();
    
    public LevelManager(ManiaCore maniaCore) {
        this.maniaCore = maniaCore;
    }
    
    public void loadFromDatabase() {
        List<IRecord> records = maniaCore.getDatabase().getRecords(LevelRecord.class, null, null);
        for (IRecord record : records) {
            if (record instanceof LevelRecord) {
                this.levels.put(record.getId(), ((LevelRecord) record).toObject());
            }
        }
    }
    
    public Map<Integer, Level> getLevels() {
        return levels;
    }
    
    public void generateDefaults() {
        if (levels.isEmpty()) {
            Level levelOne = new Level(1, 0, 100, ChatColor.GRAY);
            Level levelTwo = new Level(2, 50, 0, ChatColor.GRAY);
            Level levelThree = new Level(3, 100, 0, ChatColor.GRAY);
            Level levelFour = new Level(4, 200, 0, ChatColor.GRAY);
            Level levelFive = new Level(5, 400, 0, ChatColor.GRAY);
            Level levelSix = new Level(6, 800, 0, ChatColor.GRAY);
            Level levelSeven = new Level(7, 1600, 0, ChatColor.GRAY);
            Level levelEight = new Level(8, 3200, 0, ChatColor.GRAY);
            Level levelNine = new Level(9, 6400, 0, ChatColor.GRAY);
            Level levelTen = new Level(10, 12800, 3000, ChatColor.DARK_AQUA);
            Level levelEleven = new Level(11, 14080, 0, ChatColor.DARK_AQUA);
            Level levelTwelve = new Level(12, 15488, 0, ChatColor.DARK_AQUA);
            Level levelThirteen = new Level(13, 17036, 0, ChatColor.DARK_AQUA);
            Level levelFourteen = new Level(14, 18739, 0, ChatColor.DARK_AQUA);
            Level levelFifteen = new Level(15, 20612, 0, ChatColor.DARK_AQUA);
            Level levelSixteen = new Level(16, 22673, 0, ChatColor.DARK_AQUA);
            Level levelSeventeen = new Level(17, 24940, 0, ChatColor.DARK_AQUA);
            Level levelEighteen = new Level(18, 27434, 0, ChatColor.DARK_AQUA);
            Level levelNineteen = new Level(19, 30177, 0, ChatColor.DARK_AQUA);
            Level levelTwenty = new Level(20, 33194, 10000, ChatColor.GOLD);
            generateLevelDatabase(levelOne, levelTwo, levelThree, levelFour, levelFive, levelSix, levelSeven, levelEight, levelNine, levelTen, levelEleven, levelTwelve,
                    levelThirteen, levelFourteen, levelFifteen, levelSixteen, levelSeventeen, levelEighteen, levelNineteen, levelTwenty);
        }
    }
    
    public Level getLevel(long totalExperience) {
        Level level = null;
        
        for (Level l : this.levels.values()) {
            if (level == null) {
                if (l.getTotalXp() <= totalExperience) {
                    level = l;
                }
            } else {
                if (l.getTotalXp() > level.getTotalXp()) {
                    if (l.getTotalXp() < totalExperience) {
                        level = l;
                    }
                }
            }
        }
        
        if (level == null) {
            level = this.levels.get(1);
        }
        return level;
    }
    
    private void generateLevelDatabase(Level... levels) {
        if (levels != null) {
            for (Level level : levels) {
                maniaCore.getDatabase().addRecordToQueue(new LevelRecord(level));
            }
            
            maniaCore.getDatabase().pushQueue();
            for (Level level : levels) {
                this.levels.put(level.getNumber(), level);
            }
        }
    }
}
