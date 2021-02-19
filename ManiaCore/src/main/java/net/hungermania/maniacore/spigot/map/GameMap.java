package net.hungermania.maniacore.spigot.map;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.util.Position;
import net.hungermania.maniacore.spigot.util.SpigotUtils;
import net.hungermania.manialib.data.annotations.ColumnInfo;
import net.hungermania.manialib.data.annotations.TableInfo;
import net.hungermania.manialib.data.model.IRecord;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Getter
@TableInfo(tableName = "gamemaps")
public class GameMap implements IRecord {

    @Setter protected int id; //Database id, isn't really used outside of database things
    @Setter protected String name; //The display name of the map
    @Setter protected Position center; //The center of the map //TODO TypeHandler
    protected String[] creators; //Map creators
    @Setter protected String downloadUrl; //The url in which to download the map zip file
    protected Set<Spawn> spawns; //Spawn locations of the map
    //Temp Stuff
    @Setter @ColumnInfo(ignored = true) protected UUID uuid; //This is used for the world
    @Setter @ColumnInfo(ignored = true) protected World world; //The Bukkit World for this map and can be used to easily reference the world
    @Setter @ColumnInfo(ignored = true) protected File zipFile; //The downloaded zip file for easier reference

    public GameMap(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public GameMap(int id, String name, Position center, String[] creators, String downloadUrl, Set<Spawn> spawns) {
        this.id = id;
        this.name = name;
        this.center = center;
        this.creators = creators;
        this.downloadUrl = downloadUrl;
        this.spawns = spawns;
    }

    public void loadMap(MapManager mapManager) {
        new BukkitRunnable() {
            public void run() {
                UUID fileUUID = UUID.randomUUID();
                File tmpFile = new File(mapManager.getDownloadFolder(), fileUUID + ".tmp");
                try {
                    URL url = new URL(downloadUrl);
                    URLConnection connection = url.openConnection();
                    connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                    try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream()); FileOutputStream out = new FileOutputStream(tmpFile)) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer, 0, 1024)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    }

                    File zipFile = new File(mapManager.getDownloadFolder(), fileUUID + ".zip");
                    tmpFile.renameTo(zipFile);
                    String worldName = fileUUID.toString();
                    File worldContainer = new File(worldName);
                    if (!worldContainer.exists()) {
                        worldContainer.createNewFile();
                    }

                    try (ZipInputStream zip = new ZipInputStream(new FileInputStream(zipFile))) {
                        byte[] buffer = new byte[1024];
                        ZipEntry entry;
                        while ((entry = zip.getNextEntry()) != null) {
                            String name = entry.getName();
                            File dest = new File(worldContainer, name);
                            if (entry.isDirectory())
                                continue;
                            dest.getParentFile().mkdirs();
                            try (FileOutputStream out = new FileOutputStream(dest)) {
                                int length;
                                while ((length = zip.read(buffer)) > 0) {
                                    out.write(buffer, 0, length);
                                }
                            }
                        }
                    }

                    zipFile.delete();
                } catch (Exception e) {
                }

                new BukkitRunnable() {
                    public void run() {
                        WorldCreator creator = new WorldCreator(fileUUID.toString());
                        creator.seed(0);
                        creator.type(WorldType.FLAT);
                        creator.environment(World.Environment.NORMAL);

                        world = Bukkit.createWorld(creator);
                        world.setTime(0);
                        world.setThundering(false);
                        world.setStorm(false);
                        Location spawnpoint = SpigotUtils.positionToLocation(world, center);
                        Chunk spawnChunk = spawnpoint.getChunk();
                        for (int x = spawnChunk.getX() - 5; x < spawnChunk.getX() + 5; x++) {
                            for (int z = spawnChunk.getZ() + 5; z < spawnChunk.getZ() + 5; z++) {
                                spawnChunk.unload(true);
                                spawnChunk.load(true);
                            }
                        }

                        world.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
                    }
                }.runTask(mapManager.getPlugin());
            }
        }.runTaskAsynchronously(mapManager.getPlugin());
    }
}