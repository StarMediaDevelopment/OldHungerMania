package net.hungermania.maniacore.api.leveling;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.LevelRecord;
import net.hungermania.manialib.sql.IRecord;
import net.hungermania.manialib.util.Range;
import net.md_5.bungee.api.ChatColor;

import java.util.*;

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
            int totalXp = 2500;
    
            Set<Range<ChatColor>> levelColors = new HashSet<>();
            levelColors.add(new Range<>(1, 9, ChatColor.GRAY));
            levelColors.add(new Range<>(10, 19, ChatColor.DARK_AQUA));
            levelColors.add(new Range<>(20, 29, ChatColor.YELLOW));
            levelColors.add(new Range<>(30, 39, ChatColor.BLUE));
            levelColors.add(new Range<>(40, 49, ChatColor.AQUA));
            levelColors.add(new Range<>(50, 59, ChatColor.GREEN));
            levelColors.add(new Range<>(60, 69, ChatColor.RED));
            levelColors.add(new Range<>(70, 79, ChatColor.DARK_PURPLE));
            levelColors.add(new Range<>(80, 89, ChatColor.DARK_RED));
            levelColors.add(new Range<>(90, 99, ChatColor.LIGHT_PURPLE));
            levelColors.add(new Range<>(100, 100, ChatColor.GOLD));
            
            for (int i = 1; i <= 100; i++) {
                ChatColor color = ChatColor.GRAY;
    
                for (Range<ChatColor> range : levelColors) {
                    if (range.contains(i)) {
                        color = range.getObject();
                    }
                }
                
                int levelXp;
                if (i == 1) {
                    levelXp = totalXp;
                } else {
                    levelXp = ((i - 1) * totalXp) + (i * totalXp);
                }
                
                Level level = new Level(i, levelXp, 0, color);
                ManiaCore.getInstance().getDatabase().addRecordToQueue(new LevelRecord(level));
                this.levels.put(i, level);
            }
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
