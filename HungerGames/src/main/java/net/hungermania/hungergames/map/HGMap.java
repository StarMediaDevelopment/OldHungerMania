package net.hungermania.hungergames.map;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.util.Position;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

@Getter
public class HGMap {
    private FileConfiguration config;
    @Setter private String name, folder;
    @Setter private Position center;
    private SortedMap<Integer, Position> spawns = new TreeMap<>();
    @Setter private World world;
    @Setter private boolean editmode;
    @Setter private int borderDistance = 100;
    private List<String> creators = new ArrayList<>();
    
    public HGMap(String folder) {
        this.folder = folder;
        
        File mapFolder = new File(folder);
        if (!mapFolder.exists()) {
            throw new IllegalArgumentException("Map Folder " + folder + " does not exist");
        }
        
        for (File file : mapFolder.listFiles()) {
            if (!file.getName().equalsIgnoreCase("map.yml")) {
                continue;
            }
            
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            
            this.name = config.getString("name");
            ConfigurationSection centerSection = config.getConfigurationSection("center");
            if (centerSection != null) {
                double spawnX = centerSection.getDouble("x");
                double spawnY = centerSection.getDouble("y");
                double spawnZ = centerSection.getDouble("z");
                float spawnYaw = (float) centerSection.getDouble("yaw");
                float spawnPitch = (float) centerSection.getDouble("pitch");
                this.center = new Position(spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);
            }
            
            ConfigurationSection spawnsSection = config.getConfigurationSection("spawns");
            if (spawnsSection != null) {
                for (String s : spawnsSection.getKeys(false)) {
                    double x = spawnsSection.getDouble(s + ".x");
                    double y = spawnsSection.getDouble(s + ".y");
                    double z = spawnsSection.getDouble(s + ".z");
                    float yaw = (float) spawnsSection.getDouble(s + ".yaw");
                    float pitch = (float) spawnsSection.getDouble(s + ".pitch");
                    Integer id = Integer.parseInt(s);
                    this.spawns.put(id, new Position(x, y, z, yaw, pitch));
                }
            }
            
            if (config.contains("borderdistance")) {
                this.borderDistance = config.getInt("borderdistance");
            }
            
            if (config.contains("creators")) {
                this.creators.addAll(config.getStringList("creators"));
            }
        }
    }
    
    public boolean isSetup() {
        if (this.center == null) return false;
        return this.spawns.size() != 0;
    }
    
    public void saveData() {
        getConfig().set("name", this.name);
        if (getCenter() != null) {
            getConfig().set("center.x", getCenter().getX());
            getConfig().set("center.y", getCenter().getY());
            getConfig().set("center.z", getCenter().getZ());
            getConfig().set("center.yaw", getCenter().getYaw());
            getConfig().set("center.pitch", getCenter().getPitch());
        }
        
        getConfig().set("spawns", null);
        for (Entry<Integer, Position> entry : this.spawns.entrySet()) {
            getConfig().set("spawns." + entry.getKey() + ".x", entry.getValue().getX());
            getConfig().set("spawns." + entry.getKey() + ".y", entry.getValue().getY());
            getConfig().set("spawns." + entry.getKey() + ".z", entry.getValue().getZ());
            getConfig().set("spawns." + entry.getKey() + ".yaw", entry.getValue().getYaw());
            getConfig().set("spawns." + entry.getKey() + ".pitch", entry.getValue().getPitch());
        }
        
        getConfig().set("borderdistance", borderDistance);
        getConfig().set("creators", this.creators);
        
        saveConfig();
    }
    
    public void addCreator(String creator) {
        this.creators.add(creator);
    }
    
    public void addCreators(String... creators) {
        this.creators.addAll(Arrays.asList(creators));
    }
    
    public FileConfiguration getConfig() {
        if (this.config == null) {
            this.config = YamlConfiguration.loadConfiguration(new File(getFolder(), "map.yml"));
        }
        
        return config;
    }
    
    public void saveConfig() {
        try {
            getConfig().save(new File(getFolder(), "map.yml"));
        } catch (IOException e) {}
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        HGMap hgMap = (HGMap) o;
        return Objects.equals(name.toLowerCase().replace(" ", "_"), hgMap.name.toLowerCase().replace(" ", "_"));
    }
    
    public int hashCode() {
        return Objects.hash(name);
    }
}