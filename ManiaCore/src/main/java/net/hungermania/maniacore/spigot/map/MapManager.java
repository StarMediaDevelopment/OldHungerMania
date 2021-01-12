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
}
