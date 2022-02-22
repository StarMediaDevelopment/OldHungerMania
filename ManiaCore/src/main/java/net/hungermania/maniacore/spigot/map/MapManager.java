package net.hungermania.maniacore.spigot.map;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class MapManager {

    private Map<Integer, GameMap> cachedMaps = new HashMap<>();
    private File downloadFolder;
    private Set<UUID> usedMapUniqueIds = new HashSet<>();
    private JavaPlugin plugin;
    
    public MapManager(JavaPlugin plugin) {
        this.plugin = plugin;
        downloadFolder = new File(plugin.getDataFolder(), "downloads");
        if (!downloadFolder.exists()) {
            if (!downloadFolder.mkdirs()) {
                plugin.getLogger().severe("Could not create Map Download Folder");
            }
        }
    }
    
    public Map<Integer, GameMap> getCachedMaps() {
        return cachedMaps;
    }
    
    public File getDownloadFolder() {
        return downloadFolder;
    }
    
    public Set<UUID> getUsedMapUniqueIds() {
        return usedMapUniqueIds;
    }
    
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
