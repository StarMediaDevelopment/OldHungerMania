package net.hungermania.maniacore.spigot.map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.hungermania.maniacore.api.util.Position;
import net.hungermania.manialib.sql.*;
import net.hungermania.manialib.util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMapRecord implements IRecord<GameMap> {

    private GameMap object;
    public static Table generateTable(Database database) {
        Table table = new Table(database, "gamemaps");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("center", DataType.VARCHAR, 1000);
        table.addColumn("downloadUrl", DataType.VARCHAR, 1000);
        table.addColumn("name", DataType.VARCHAR, 100);
        table.addColumn("spawns", DataType.VARCHAR, 10000);
        table.addColumn("creators", DataType.VARCHAR, 1000);
        return table;
    }

    public GameMapRecord(GameMap object) {
        this.object = object;
    }

    public GameMapRecord(Row row) {
        int id = row.getInt("id");
        String downloadUrl = row.getString("downloadUrl");
        String name = row.getString("name");
        String rawCenter = row.getString("center");
        String[] centerSplit = rawCenter.split(",");
        double cx = Double.parseDouble(centerSplit[0]);
        double cy = Double.parseDouble(centerSplit[1]);
        double cz = Double.parseDouble(centerSplit[2]);
        float cyaw = Float.parseFloat(centerSplit[3]);
        float cp = Float.parseFloat(centerSplit[4]);
        Position center = new Position(cx, cy, cz, cyaw, cp);
        JsonObject spawnsObject = (JsonObject) new JsonParser().parse(row.getString("spawns"));
        Map<Integer, Position> spawns = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : spawnsObject.entrySet()) {
            Integer index = Integer.parseInt(entry.getKey());
            JsonObject spawnEntry = (JsonObject) entry.getValue();
            double x = spawnEntry.get("x").getAsDouble();
            double y = spawnEntry.get("y").getAsDouble();
            double z = spawnEntry.get("z").getAsDouble();
            float yaw = spawnEntry.get("yaw").getAsFloat();
            float pitch = spawnEntry.get("pitch").getAsFloat();
            spawns.put(index, new Position(x, y, z, yaw, pitch));
        }
        List<String> creators = Arrays.asList(row.getString("creators").split(","));
        this.object = new GameMap(id, name, center, creators, downloadUrl, spawns);
    }

    public int getId() {
        return object.getId();
    }

    public void setId(int id) {
        object.setId(id);
    }

    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", object.getId());
            put("downloadUrl", object.getDownloadUrl());
            put("name", object.getName());
            put("center", object.getCenter().getX() + "," + object.getCenter().getY() + "," + object.getCenter().getZ() + "," + object.getCenter().getYaw() + "," + object.getCenter().getPitch());
            JsonObject spawns = new JsonObject();
            for (Entry<Integer, Position> entry : object.getSpawns().entrySet()) {
                JsonObject spawn = new JsonObject();
                spawn.addProperty("x", entry.getValue().getX());
                spawn.addProperty("y", entry.getValue().getY());
                spawn.addProperty("z", entry.getValue().getZ());
                spawn.addProperty("yaw", entry.getValue().getYaw());
                spawn.addProperty("pitch", entry.getValue().getPitch());
                spawns.add(entry.getKey() + "", spawn);
            }
            put("spawns", spawns.getAsString());
            put("creators", Utils.join(object.getCreators(), ","));
        }};
    }

    public GameMap toObject() {
        return object;
    }
}
