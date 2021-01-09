package net.hungermania.maniacore.spigot.map;

import net.hungermania.maniacore.api.util.Position;
import org.bukkit.World;

import java.io.File;
import java.util.*;

public class GameMap {
    
    private int borderDistance = 50; //Deathmatch world border 
    private Position center; //The center of the map
    private List<String> creators = new ArrayList<>(); //Map creators
    private String downloadUrl; //The url in which to download the map zip file
    private int id; //Database id, isn't really used outside of database things
    private String name; //The display name of the map
    private Map<Integer, Position> spawns = new HashMap<>(); //Spawn locations of the map
    //Temp Stuff
    private UUID uuid; //This is used for the world
    private World world; //The Bukkit World for this map and can be used to easily reference the world
    private File zipFile; //The downloaded zip file for easier reference
}
