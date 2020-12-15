package net.hungermania.hub.leaderboard;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import lombok.Getter;
import net.hungermania.hub.Hub;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.manialib.util.Range;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class LeaderboardManager {
    
    private final Hub plugin;
    @Getter private Map<Integer, NPC> npcs = new HashMap<>();
    @Getter private Set<Range<Leaderboard>> leaderboards = new HashSet<>();
    private File file;
    private FileConfiguration config;
    
    public LeaderboardManager(Hub plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "leaderboards.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create the file leaderboards.yml");
            }
        }
        
        this.config = YamlConfiguration.loadConfiguration(file);
    }
    
    public void addLeaderboard(Leaderboard leaderboard) {
        this.leaderboards.add(new Range<>(leaderboard.getMin(), leaderboard.getMax(), leaderboard));
    }
    
    public void addNPC(NPC npc) {
        this.npcs.put(npc.getPosition(), npc);
    }
    
    public void loadData() {
        if (this.config.contains("npcs")) {
            ConfigurationSection section = config.getConfigurationSection("npcs");
            for (String key : section.getKeys(false)) {
                UUID uuid  = UUID.fromString(section.getString(key + ".uuid"));
                UUID skinUUID = UUID.fromString(section.getString(key + ".skinUuid"));
                Location location = (Location) section.get(key + ".location");
                int position = Integer.parseInt(key);
                NPC npc = new NPC(position, uuid, ManiaCore.getInstance().getSkinManager().getSkin(skinUUID), location);
                this.npcs.put(position, npc);
            }
        }
        
        if (this.config.contains("holograms")) {
            ConfigurationSection hologramsSection = config.getConfigurationSection("holograms");
            for (String key : hologramsSection.getKeys(false)) {
                int min = hologramsSection.getInt(key + ".min");
                int max = hologramsSection.getInt(key + ".max");
                Location location = (Location) hologramsSection.get(key + ".location");
                Leaderboard leaderboard = new Leaderboard(location, min, max);
                this.leaderboards.add(new Range<>(min, max, leaderboard));
                Hologram hologram = HologramsAPI.createHologram(plugin, leaderboard.getLocation());
                leaderboard.setHologram(hologram);
            }
        }
    }
    
    public void saveData(boolean remove) {
        for (Entry<Integer, NPC> entry : this.npcs.entrySet()) {
            this.config.set("npcs." + entry.getKey() + ".uuid", entry.getValue().getUuid().toString());
            this.config.set("npcs." + entry.getKey() + ".skinUuid", entry.getValue().getSkin().getUuid());
            this.config.set("npcs." + entry.getKey() + ".location", entry.getValue().getLocation());
        }
        
        for (Range<Leaderboard> leaderboardRange : this.leaderboards) {
            String key = "holograms." + leaderboardRange.getMin() + "-" + leaderboardRange.getMax();
            this.config.set(key + ".min", leaderboardRange.getMin());
            this.config.set(key + ".max", leaderboardRange.getMax());
            this.config.set(key + ".location", leaderboardRange.getObject().getLocation());
            if (remove) {
                leaderboardRange.getObject().getHologram().delete();
            }
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            ManiaCore.getInstance().getLogger().severe("Could not save the file leaderboards.yml");
        }
    }
}