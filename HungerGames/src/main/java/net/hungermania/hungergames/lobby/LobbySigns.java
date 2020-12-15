package net.hungermania.hungergames.lobby;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.hungergames.HungerGames;
import net.hungermania.hungergames.map.HGMap;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.Position;
import net.hungermania.maniacore.api.util.Utils;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Getter
public class LobbySigns {
    
    @Setter
    private Position voteTitleSign, votingInfo;
    private Map<Integer, Position> mapSigns = new HashMap<>();
    private Map<SignType, Position> statSigns = new HashMap<>();
    private Map<Integer, Position> playerSigns = new HashMap<>();
    
    private File file;
    private FileConfiguration config;
    
    public LobbySigns() {
        HungerGames plugin = HungerGames.getInstance();
        file = new File(plugin.getDataFolder(), "signs.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return;
            }
        }
        
        config = YamlConfiguration.loadConfiguration(file);
        
        if (config.contains("votetitlesign")) {
            this.voteTitleSign = new Position(config.getDouble("votetitlesign.x"), config.getDouble("votetitlesign.y"), config.getDouble("votetitlesign.z"));
        }
        
        if (config.contains("votinginfosign")) {
            this.voteTitleSign = new Position(config.getDouble("votinginfosign.x"), config.getDouble("votinginfosign.y"), config.getDouble("votinginfosign.z"));
        }
        
        if (config.contains("mapsigns")) {
            ConfigurationSection mapSection = config.getConfigurationSection("mapsigns");
            for (String p : mapSection.getKeys(false)) {
                int position = Integer.parseInt(p);
                this.mapSigns.put(position, new Position(mapSection.getDouble(p + ".x"), mapSection.getDouble(p + ".y"), mapSection.getDouble(p + ".z")));
            }
        }
    }
    
    public void save() {
        if (voteTitleSign != null) {
            config.set("votetitlesign.x", voteTitleSign.getX());
            config.set("votetitlesign.y", voteTitleSign.getY());
            config.set("votetitlesign.z", voteTitleSign.getZ());
        }
        
        if (votingInfo != null) {
            config.set("votinginfosign.x", votingInfo.getX());
            config.set("votinginfosign.y", votingInfo.getY());
            config.set("votinginfosign.z", votingInfo.getZ());
        }
        
        if (!mapSigns.isEmpty()) {
            for (Entry<Integer, Position> entry : mapSigns.entrySet()) {
                config.set("mapsigns." + entry.getKey() + ".x", entry.getValue().getX());
                config.set("mapsigns." + entry.getKey() + ".y", entry.getValue().getY());
                config.set("mapsigns." + entry.getKey() + ".z", entry.getValue().getZ());
            }
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void updateSigns() {
        World world = Bukkit.getWorld("world");
        HungerGames plugin = HungerGames.getInstance();
        ManiaCore maniaCore = plugin.getManiaCore();
        Lobby lobby = plugin.getLobby();
        if (plugin.getGameManager().getCurrentGame() == null) {
            if (voteTitleSign != null) {
                Location location = SpigotUtils.positionToLocation(world, voteTitleSign);
                if (world.getBlockAt(location).getState() instanceof Sign) {
                    Sign sign = (Sign) world.getBlockAt(location).getState();
                    sign.setLine(0, Utils.color("&m--------------"));
                    sign.setLine(1, Utils.color("&lVote for a"));
                    sign.setLine(2, Utils.color("&lMap!"));
                    sign.setLine(3, Utils.color("&m--------------"));
                    sign.update();
                }
            }
    
            if (votingInfo != null) {
                Location location = SpigotUtils.positionToLocation(world, votingInfo);
                if (world.getBlockAt(location).getState() instanceof Sign) {
                    Sign sign = (Sign) world.getBlockAt(location).getState();
                    sign.setLine(0, Utils.color("&nVoting Power"));
                    sign.setLine(2, Utils.color("&nTime Left"));
                    if (lobby.getVoteTimer() != null) {
                        sign.setLine(3, lobby.getVoteTimer().getRemainingSeconds() + "");
                    } else {
                        sign.setLine(3, lobby.getGameSettings().getStartTimer() + "");
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        User user = maniaCore.getUserManager().getUser(player.getUniqueId());
                        Rank rank = user.getRank();
                        sign.setLine(1, rank.getVoteWeight() + "");
                        player.sendSignChange(sign.getLocation(), sign.getLines());
                    }
                }
            }
    
            if (!mapSigns.isEmpty()) {
                for (Entry<Integer, Position> entry : mapSigns.entrySet()) {
                    HGMap map = lobby.getMapOptions().getMaps().get(entry.getKey());
                    if (map == null) continue;
                    Location location = SpigotUtils.positionToLocation(world, entry.getValue());
                    if (world.getBlockAt(location).getState() instanceof Sign) {
                        Sign sign = (Sign) world.getBlockAt(location).getState();
                        String mapName;
                        if (map.getName().length() > 16) {
                            mapName = map.getName().substring(0, 15);
                        } else {
                            mapName = map.getName();
                        }
                        sign.setLine(1, mapName);
                        int votes = lobby.getMapOptions().getVotes(map);
                        sign.setLine(3, Utils.color("&n" + votes + " Vote(s)"));
    
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (lobby.getMapOptions().hasVoted(player.getUniqueId())) {
                                sign.setLine(0, Utils.color("&n#" + entry.getKey()));
                                if (map.equals(lobby.getMapOptions().getVotedMap(player.getUniqueId()))) {
                                    sign.setLine(2, Utils.color("&2&lVOTED!"));
                                } else {
                                    sign.setLine(2, "");
                                }
                            } else {
                                sign.setLine(0, Utils.color("&nClick to Vote"));
                                sign.setLine(2, "");
                            }
                            player.sendSignChange(location, sign.getLines());
                        }
                    }
                }
        
            }
        }
//        if (!statSigns.isEmpty()) {
//
//        }
//
//        if (!playerSigns.isEmpty()) {
//
//        }
    }
}