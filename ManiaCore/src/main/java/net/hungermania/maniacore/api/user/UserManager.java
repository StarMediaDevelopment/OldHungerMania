package net.hungermania.maniacore.api.user;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.*;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.stats.Stat;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.user.toggle.Toggle;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.manialib.sql.IRecord;

import java.util.*;

public abstract class UserManager {
    public User getUser(String name) {
        if (name == null && name.isEmpty()) { return null; }
        UUID uuid = Redis.getUUIDFromName(name);
        User user = getUser(uuid);
        if (user == null) {
            List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(UserRecord.class, "name", name);
            if (!records.isEmpty()) {
                user = constructUser(((UserRecord) records.get(0)).toObject());
            } else {
                user = constructUser(ManiaUtils.getUUIDFromName(name), name);
                ManiaCore.getInstance().getDatabase().pushRecord(new UserRecord(user));
            }
            user = getUser(user.getUniqueId()); //TODO Inefficient, temporary fix, trying to find the cause
        }
        
        return user;
    }
    
    public void loadIgnoredPlayers(User user) {
        List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(IgnoreInfoRecord.class, "player", user.getUniqueId().toString());
        Set<IgnoreInfo> ignoredPlayers = new HashSet<>();
        if (!records.isEmpty()) {
            for (IRecord record : records) {
                if (record instanceof IgnoreInfoRecord) {
                    ignoredPlayers.add(((IgnoreInfoRecord) record).toObject());
                }
            }
        }
        
        user.setIgnoredPlayers(ignoredPlayers);
    }
    
    public User getUser(int userId) {
        if (userId < 1) { return null; }
        UUID uuid = Redis.getUUIDFromID(userId);
        User user = getUser(uuid);
        
        if (user == null) {
            List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(UserRecord.class, "id", userId);
            if (!records.isEmpty()) {
                user = constructUser(((UserRecord) records.get(0)).toObject());
                Redis.pushUser(user);
            }
        }
        
        return user;
    }
    
    public User getUser(UUID uuid) {
        if (uuid == null) { return null; }
    
        System.out.println("Loaded user " + uuid.toString());
        Map<String, String> redisData = Redis.getUserData(uuid);
        Map<String, Statistic> stats = new HashMap<>();
        Map<Toggles, Toggle> toggles = new HashMap<>();
        User user;
        if (!redisData.isEmpty()) {
            System.out.println("Redis contains user data, loading...");
            user = constructUser(redisData);
            stats = Redis.getUserStats(uuid);
            toggles = Redis.getToggles(uuid);
            System.out.println("Loaded user data from redis");
        } else {
            System.out.println("Redis does not contain user data, loading from main database");
            List<IRecord> userRecords = ManiaCore.getInstance().getDatabase().getRecords(UserRecord.class, "uniqueId", uuid.toString());
            if (!userRecords.isEmpty()) {
                System.out.println("Database contains user data, loading...");
                user = constructUser(((UserRecord) userRecords.get(0)).toObject());
                System.out.println("Loaded from database");
            } else {
                System.out.println("Database does not contain user data, creating");
                user = constructUser(uuid, ManiaUtils.getNameFromUUID(uuid));
                System.out.println("Created new user data, saving to database...");
                ManiaCore.getInstance().getDatabase().pushRecord(new UserRecord(user));
                System.out.println("Saved successfully");
            }
        
            System.out.println("User: " + user.getName());
            List<IRecord> statsRecords = ManiaCore.getInstance().getDatabase().getRecords(StatRecord.class, "uuid", uuid.toString());
        
            if (!statsRecords.isEmpty()) {
                for (IRecord statsRecord : statsRecords) {
                    Statistic statistic = ((StatRecord) statsRecord).toObject();
                    stats.put(statistic.getName(), statistic);
                }
            }
        
            List<IRecord> togglesRecords = ManiaCore.getInstance().getDatabase().getRecords(ToggleRecord.class, "uuid", uuid.toString());
            if (!togglesRecords.isEmpty()) {
                for (IRecord togglesRecord : togglesRecords) {
                    Toggle toggle = ((ToggleRecord) togglesRecord).toObject();
                    Toggles type = Toggles.valueOf(toggle.getName().toUpperCase());
                    toggles.put(type, toggle);
                }
            }
        }
    
        for (Stat value : Stat.REGISTRY.values()) {
            if (!stats.containsKey(value.getName())) {
                Statistic statistic = value.create(uuid);
                ManiaCore.getInstance().getDatabase().pushRecord(new StatRecord(statistic));
                stats.put(value.getName(), statistic);
            }
        }
    
        for (Toggles value : Toggles.values()) {
            if (!toggles.containsKey(value)) {
                Toggle toggle = value.create(uuid);
                ManiaCore.getInstance().getDatabase().pushRecord(new ToggleRecord(toggle));
                toggles.put(value, toggle);
            }
        }
    
        user.setStats(stats);
        user.setToggles(toggles);
        
        if (user.getName() == null || user.getName().equals("") || user.getName().equals("null")) {
            user.setName(ManiaUtils.getNameFromUUID(uuid));
            ManiaCore.getInstance().getDatabase().pushRecord(new UserRecord(user));
        }
        Redis.pushUser(user);
        return user;
    }
    
    public abstract User constructUser(UUID uuid, String name);
    
    public abstract User constructUser(Map<String, String> data);
    
    public abstract User constructUser(User user);
}