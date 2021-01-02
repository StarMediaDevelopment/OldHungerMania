package net.hungermania.hungergames.map;

import net.hungermania.hungergames.HungerGames;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.api.util.Position;
import net.hungermania.manialib.util.Utils;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MapManager implements CommandExecutor {
    private File MAPS_FOLDER;
    
    private HungerGames plugin;
    private Map<String, HGMap> maps = new HashMap<>();
    private Set<String> unsetupMaps = new HashSet<>();
    
    public MapManager(HungerGames plugin) {
        this.plugin = plugin;
        
        MAPS_FOLDER = new File(plugin.getConfig().getString("maps_folder"));
        if (!MAPS_FOLDER.exists()) {
            plugin.getLogger().severe("Folder for the maps does not exist.");
        }
    }
    
    public World copyMap(String name) {
        HGMap map = this.maps.get(name.toLowerCase() .replace(" ", "_"));
        if (map == null) return null;
        
        String worldName = map.getName().toLowerCase().replace(" ", "_");
        File parentFile = new File(plugin.getDataFolder() + File.separator + ".." + File.separator + "..");
        File destination = new File(parentFile + File.separator + worldName);
        if (!destination.exists()) {
            destination.mkdir();
        }
        
        if (destination.exists()) {
            for (World world : Bukkit.getWorlds()) {
                if (world.getWorldFolder().equals(destination)) {
                    Bukkit.unloadWorld(world, false);
                }
            }
            Utils.purgeDirectory(destination);
        }
    
        Utils.copyFolder(new File(map.getFolder()).toPath(), destination.toPath());
        World world = new WorldCreator(worldName).createWorld();
        map.setWorld(world);
        map.getWorld().setAutoSave(false);
        return world;
    }
    
    public void loadMaps() {
        this.unsetupMaps.clear();
        if (this.MAPS_FOLDER.exists()) {
            maps:
            for (File file : MAPS_FOLDER.listFiles()) {
                if (file.isDirectory()) {
                    for (File mapFile : file.listFiles()) {
                        if (mapFile.getName().equalsIgnoreCase("map.yml")) {
                            HGMap hgMap = new HGMap(file.getAbsolutePath());
                            if (!this.maps.containsKey(hgMap.getName().toLowerCase().replace(" ", "_"))) {
                                this.maps.put(hgMap.getName().toLowerCase().replace(" ", "_"), hgMap);
                            }
                            continue maps;
                        }
                    }
            
                    this.unsetupMaps.add(file.getAbsolutePath());
                }
            }
        }
    
        File parentFile = new File(plugin.getDataFolder() + File.separator + ".." + File.separator + "..");
        for (HGMap map : maps.values()) {
            String worldName = map.getName().toLowerCase().replace(" ", "_");
            for (File file : parentFile.listFiles()) {
                if (file.isDirectory()) {
                    if (file.getName().equalsIgnoreCase(worldName)) {
                        Utils.purgeDirectory(file);
                    }
                }
            }
        }
    }
    
    public Map<String, HGMap> getMaps() {
        return maps;
    }
    
    public Set<String> getUnsetupMaps() {
        return unsetupMaps;
    }
    
    public void deleteMap(String name) {
        System.out.println("Attempting to delete " + name);
        HGMap map = this.maps.get(name.toLowerCase().replace(" ", "_"));
        if (map == null) return;
        System.out.println("Valid map found for " + name);
        if (map.getWorld() == null) return;
        System.out.println("World exists for map " + map.getName());
        File worldFolder = map.getWorld().getWorldFolder();
        System.out.println("World folder is " + worldFolder);
        Bukkit.unloadWorld(map.getWorld(), false);
        System.out.println("Unloaded world " + map.getName());
        map.setWorld(null);
        try {
            System.out.println("Attempting to delete files");
            FileDeleteStrategy.FORCE.delete(worldFolder);
            System.out.println("Files deleted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            User user = plugin.getManiaCore().getUserManager().getUser(((Player) sender).getUniqueId());
            if (!user.hasPermission(Rank.ADMIN)) {
                user.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(ManiaUtils.color("&cYou must provide a subcommand"));
            return true;
        }
    
        if (ManiaUtils.checkCmdAliases(args, 0, "listnotsetup", "lns")) {
            if (this.unsetupMaps.isEmpty()) {
                sender.sendMessage(ManiaUtils.color("&cThere are no maps that are not setup."));
                return true;
            }
        
            sender.sendMessage(ManiaUtils.color("&6Map Directories that do not have a map.yml file."));
            for (String string : this.unsetupMaps) {
                sender.sendMessage(ManiaUtils.color("    &8- &e" + string));
            }
        } else if (ManiaUtils.checkCmdAliases(args, 0, "create")) {
            if (this.unsetupMaps.isEmpty()) {
                sender.sendMessage(ManiaUtils.color("&cThere are no maps that are not setup."));
                return true;
            }
        
            if (!(args.length > 0)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a map name. This can be the full path, or just the name of the folder."));
                return true;
            }
            
            File baseFolder = null;
            for (String string : this.unsetupMaps) {
                if (string.equalsIgnoreCase(args[1].replace("_", " ")) || string.toLowerCase().contains(args[1].toLowerCase().replace("_", " "))) {
                    baseFolder = new File(string);
                    break;
                }
            }
            
            if (baseFolder == null) {
                sender.sendMessage(ManiaUtils.color("&cThere was a problem getting the base folder of that map name."));
                return true;
            }
            
            if (!baseFolder.exists()) {
                sender.sendMessage(ManiaUtils.color("&cThe base folder for that map name does not exist."));
                return true;
            }
            
            if (!(args.length > 2)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a name for the map."));
                return true;
            }
            
            String name = StringUtils.join(args, " ", 2, args.length);
            if (this.maps.containsKey(name.toLowerCase().replace(" ", "_"))) {
                sender.sendMessage(ManiaUtils.color("&cThere is already a map with that name."));
                return true;
            }
            
            File mapYaml = new File(baseFolder, "map.yml");
            try {
                mapYaml.createNewFile();
            } catch (IOException e) {
                sender.sendMessage(ManiaUtils.color("&cThere was an error creating the map.yml file: " + e.getMessage()));
                return true;
            }
    
            FileConfiguration config = YamlConfiguration.loadConfiguration(mapYaml);
            config.set("name", name);
            try {
                config.save(mapYaml);
            } catch (IOException e) {
                sender.sendMessage("&cThere was an error saving the map.yml file.");
                return true;
            }
            
            HGMap hgMap = new HGMap(baseFolder.getAbsolutePath());
            this.maps.put(hgMap.getName().toLowerCase().replace(" ", "_"), hgMap);
            sender.sendMessage(ManiaUtils.color("&aSuccessfully created a map called &b" + name));
            sender.sendMessage(ManiaUtils.color("&aNow you must use other commands to set the spawn locations and the center."));
        } else {
            HGMap hgMap = this.maps.get(args[0].toLowerCase());
            if (hgMap == null) {
                sender.sendMessage(ManiaUtils.color("&cA map by that name does not exist."));
                return true;
            }
            
            if (!(args.length > 1)) {
                sender.sendMessage(ManiaUtils.color("&cYou must provide a modification command"));
                return true;
            }
        
            if (ManiaUtils.checkCmdAliases(args, 1, "editmode")) {
                hgMap.setEditmode(!hgMap.isEditmode());
                if (hgMap.isEditmode()) {
                    if (copyMap(hgMap.getName()) != null) {
                        sender.sendMessage(ManiaUtils.color("&aYou enabled editmode for the map &b" + hgMap.getName()));
                    } else {
                        sender.sendMessage(ManiaUtils.color("&cThere was an error enabling edit mode"));
                    }
                } else {
                    if (hgMap.getCenter() == null) {
                        sender.sendMessage(ManiaUtils.color("&cThere is no map center set."));
                        return true;
                    }
                    
                    if (hgMap.getSpawns().isEmpty()) {
                        sender.sendMessage(ManiaUtils.color("&cThere are no spawnpoints set."));
                        return true;
                    }
                    
                    if (hgMap.getCreators().isEmpty()) {
                        sender.sendMessage(ManiaUtils.color("&cThere are no creators configured."));
                        return true;
                    }
                    
                    for (Player player : hgMap.getWorld().getPlayers()) {
                        player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
                    }
                    hgMap.saveData();
                    this.unsetupMaps.remove(hgMap.getFolder());
                    deleteMap(hgMap.getName());
                    sender.sendMessage(ManiaUtils.color("&aYou disabled editmode for the map &b" + hgMap.getName()));
                }
                return true;
            }
            
            if (!hgMap.isEditmode()) {
                sender.sendMessage(ManiaUtils.color("&cThat map is not in edit mode."));
                return true;
            }
        
            if (ManiaUtils.checkCmdAliases(args, 1, "setspawn", "removespawn", "ss", "rs")) {
                if (!(args.length > 2)) {
                    sender.sendMessage(ManiaUtils.color("&cYou must provide a spawn position"));
                    return true;
                }
            
                int position;
                try {
                    position = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ManiaUtils.color("&cThe value you provided for the position is not a valid number."));
                    return true;
                }
            
                if (ManiaUtils.checkCmdAliases(args, 1, "removespawn", "rs")) {
                    if (!hgMap.getSpawns().containsKey(position)) {
                        sender.sendMessage(ManiaUtils.color("&cThat map does not have a spawn point at that index number."));
                        return true;
                    }
                
                    hgMap.getSpawns().remove(position);
                    sender.sendMessage(ManiaUtils.color("&aYou removed the spawnpoint &b" + position));
                } else {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ManiaUtils.color("&cOnly players can use that command."));
                        return true;
                    }
                
                    Player player = (Player) sender;
                    Position pos = getPosition(player);
                    hgMap.getSpawns().put(position, pos);
                    player.sendMessage(ManiaUtils.color("&aYou set your location to the spawn point &b" + position));
                }
            } else if (ManiaUtils.checkCmdAliases(args, 1, "addspawn", "as")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ManiaUtils.color("&cOnly players can use that command."));
                    return true;
                }
            
                Player player = (Player) sender;
                Position position = getPosition(player);
                int pos = 0;
                try {
                    pos = hgMap.getSpawns().lastKey() + 1;
                } catch (NoSuchElementException e) {}
                hgMap.getSpawns().put(pos, position);
                player.sendMessage(ManiaUtils.color("&aYou added your current location as spawnpoint &b" + pos));
            } else if (ManiaUtils.checkCmdAliases(args, 1, "setname", "sn")) {
                if (!(args.length > 2)) {
                    sender.sendMessage(ManiaUtils.color("&cYou must provide a name."));
                    return true;
                }
            
                String name = StringUtils.join(args, " ", 2, args.length);
                if (this.maps.containsKey(name.toLowerCase().replace(" ", "_"))) {
                    sender.sendMessage(ManiaUtils.color("&cThere is already a map with that name."));
                    return true;
                }
            
                String oldName = hgMap.getName();
                hgMap.setName(name);
                this.maps.remove(oldName.toLowerCase());
                this.maps.put(name.toLowerCase().replace(" ", "_"), hgMap);
                sender.sendMessage(ManiaUtils.color("&aYou renamed the map &b" + oldName + " &ato " + name));
            } else if (ManiaUtils.checkCmdAliases(args, 1, "teleport", "tp")) {
                if (hgMap.getWorld() == null) {
                    sender.sendMessage(ManiaUtils.color("&cThat map is not loaded."));
                    return true;
                }
            
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ManiaUtils.color("&cOnly players can use that command."));
                    return true;
                }
            
                Player player = (Player) sender;
                player.teleport(hgMap.getWorld().getSpawnLocation());
                player.sendMessage(ManiaUtils.color("&aYou were teleported to the map &b" + hgMap.getName()));
                player.setGameMode(GameMode.CREATIVE);
            } else if (ManiaUtils.checkCmdAliases(args, 1, "setcenter", "sc")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ManiaUtils.color("&cOnly players can use that command."));
                    return true;
                }
            
                Player player = (Player) sender;
                Position position = getPosition(player);
                hgMap.setCenter(position);
                hgMap.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
                player.sendMessage(ManiaUtils.color("&aYou have set your location as the center of the map."));
            } else if (ManiaUtils.checkCmdAliases(args, 1, "addcreator", "ac")) {
                if (!(args.length > 2)) {
                    sender.sendMessage(ManiaUtils.color("&cYou must provide at least one name"));
                    return true;
                }
            
                String rawCreators = StringUtils.join(args, " ", 2, args.length);
                String creators;
            
                if (!rawCreators.contains(",")) {
                    hgMap.addCreator(rawCreators);
                    creators = rawCreators;
                } else {
                    String[] names = rawCreators.split(",");
                    for (String name : names) {
                        hgMap.addCreator(name);
                    }
                    creators = StringUtils.join(names, ", ");
                }
            
                sender.sendMessage(ManiaUtils.color("&aYou added &b" + creators + " &aas a(the) creator(s)."));
            } else if (ManiaUtils.checkCmdAliases(args, 1, "setdistance", "sd")) {
                if (!(args.length > 2)) {
                    sender.sendMessage(ManiaUtils.color("&cYou must provide a distance"));
                    return true;
                }
            
                int distance;
                try {
                    distance = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ManiaUtils.color("&cThe value you provided for the distance is not a valid number."));
                    return true;
                }
                
                hgMap.setBorderDistance(distance);
                sender.sendMessage(ManiaUtils.color("&aYou have set the border distance to &b" + distance));
            }
        }
        
        return true;
    }
    
    private Position getPosition(Player player) {
        double x = player.getLocation().getBlockX() + .5;
        double y = player.getLocation().getBlockY() + 1;
        double z = player.getLocation().getBlockZ() + .5;
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        return new Position(x, y, z, yaw, pitch);
    }
}