package net.hungermania.maniacore.api.user;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.IgnoreInfoRecord;
import net.hungermania.maniacore.api.records.StatRecord;
import net.hungermania.maniacore.api.records.ToggleRecord;
import net.hungermania.maniacore.api.records.UserRecord;
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
        System.out.println("Getting user by name " + name);
        if (name == null && name.isEmpty()) { return null; }
        UUID uuid = Redis.getUUIDFromName(name);
        System.out.println("UUID is " + uuid);
        User user = getUser(uuid);
        System.out.println("User is " + user);
        if (user == null) {
            System.out.println("User is null, loading from database");
            List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(UserRecord.class, "name", name);
            System.out.println("Record size " + records.size());
            if (!records.isEmpty()) {
                System.out.println("Record exists, constructing");
                user = constructUser(((UserRecord) records.get(0)).toObject());
                System.out.println("User is " + user);
            } else {
                System.out.println("No record exists");
                user = constructUser(ManiaUtils.getUUIDFromName(name), name);
                System.out.println("User is " + user);
                ManiaCore.getInstance().getDatabase().pushRecord(new UserRecord(user));
                System.out.println("Pushed to database");
            }
            System.out.println("Loading data based on UUID");
            user = getUser(user.getUniqueId()); //TODO Inefficient, temporary fix, trying to find the cause
            System.out.println("Loaded, pushing to Redis");
            Redis.pushUser(user);
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
    
        System.out.println("Loading user " + uuid);
        Map<String, String> redisData = Redis.getUserData(uuid);
        Map<String, Statistic> stats = new HashMap<>();
        Map<String, Statistic> fakedStats = new HashMap<>();
        Map<Toggles, Toggle> toggles = new HashMap<>();
        User user;
        if (!redisData.isEmpty()) {
            System.out.println("Redis contains user data, loading...");
            user = constructUser(redisData);
            stats = Redis.getUserStats(uuid);
            toggles = Redis.getToggles(uuid);
            fakedStats = Redis.getUserFakedStats(uuid);
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

            System.out.println("Stats Records Size: " + statsRecords.size());
            if (!statsRecords.isEmpty()) {
                System.out.println("Loading stats");
                for (IRecord statsRecord : statsRecords) {
                    Statistic statistic = ((StatRecord) statsRecord).toObject();
                    stats.put(statistic.getName(), statistic);
                }
                System.out.println("Loaded " + stats.size() + " stats");
            }
        
            List<IRecord> togglesRecords = ManiaCore.getInstance().getDatabase().getRecords(ToggleRecord.class, "uuid", uuid.toString());
            System.out.println("Toggles Record size: " + togglesRecords.size());
            if (!togglesRecords.isEmpty()) {
                System.out.println("Loading Toggles");
                for (IRecord togglesRecord : togglesRecords) {
                    Toggle toggle = ((ToggleRecord) togglesRecord).toObject();
                    Toggles type = Toggles.valueOf(toggle.getName().toUpperCase());
                    toggles.put(type, toggle);
                }
                System.out.println("Loaded " + toggles.size() + " toggles");
            }
        }

        System.out.println("Creating default stat information for missing stats");
        for (Stat value : Stat.REGISTRY.values()) {
            if (!stats.containsKey(value.getName())) {
                Statistic statistic = value.create(uuid);
                ManiaCore.getInstance().getDatabase().pushRecord(new StatRecord(statistic));
                stats.put(value.getName(), statistic);
            }
        }
        System.out.println("Completed");

        System.out.println("Creating default toggle information for default toggles");
        for (Toggles value : Toggles.values()) {
            if (!toggles.containsKey(value)) {
                Toggle toggle = value.create(uuid);
                ManiaCore.getInstance().getDatabase().pushRecord(new ToggleRecord(toggle));
                toggles.put(value, toggle);
            }
        }
        System.out.println("Completed");

        System.out.println("Setting in memory information");
        user.setStats(stats);
        user.setToggles(toggles);
        user.setFakeStats(fakedStats);
        System.out.println("Done");
        
        if (user.getName() == null || user.getName().equals("") || user.getName().equals("null")) {
            System.out.println("updated user name");
            user.setName(ManiaUtils.getNameFromUUID(uuid));
            ManiaCore.getInstance().getDatabase().pushRecord(new UserRecord(user));
        }
        System.out.println("Pushing user to redis");
        Redis.pushUser(user);
        System.out.println("Pushed user to redis");
        return user;
    }
    
    public abstract User constructUser(UUID uuid, String name);
    
    public abstract User constructUser(Map<String, String> data);
    
    public abstract User constructUser(User user);
}