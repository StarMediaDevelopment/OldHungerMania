package net.hungermania.maniacore.spigot.map;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

@Getter
public class MapManager {
    private Map<Integer, GameMap> cachedMaps = new HashMap<>();
    private File downloadFolder;
    private Set<UUID> usedMapUniqueIds = new HashSet<>();
    
    public MapManager(JavaPlugin plugin) {
        downloadFolder = new File(plugin.getDataFolder(), "downloads");
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs();
        }
    }
}