package net.hungermania.maniacore.api.redis;

import com.google.gson.Gson;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.friends.FriendNotification;
import net.hungermania.maniacore.api.friends.FriendRequest;
import net.hungermania.maniacore.api.friends.Friendship;
import net.hungermania.maniacore.api.server.NetworkType;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.user.toggle.Toggle;
import net.hungermania.maniacore.api.user.toggle.Toggles;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("DuplicatedCode")
public class Redis {
    
    public static String CROSSTALK_CHANNEL;
    
    private static JedisPool source;
    private static Jedis crosstalk;
    private static CommandSubscriber subscriber;
    private static List<RedisListener> listeners = new ArrayList<>();
    
    private static File propertiesFile;
    private static Properties properties;
    
    private static String host;
    private static int port;
    private static String password;
    
    public static void startRedis() {
        CROSSTALK_CHANNEL = "Mania-Crosstalk";
        
        properties = new Properties();
        propertiesFile = new File("./mania-redis.properties");
        if (!propertiesFile.exists()) {
            ManiaCore.getInstance().getLogger().severe("No Redis connection info found, creating defaults");
            
            properties.setProperty("redis-host", "localhost");
            properties.setProperty("redis-port", "6379");
            properties.setProperty("redis-password", "");
            
            try {
                propertiesFile.createNewFile();
                
                FileOutputStream out = new FileOutputStream(propertiesFile);
                properties.store(out, "HungerMania - Redis Connection Info. Do not reproduce");
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileInputStream in = new FileInputStream(propertiesFile);
                properties.load(in);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        host = properties.getProperty("redis-host");
        port = Integer.parseInt(properties.getProperty("redis-port"));
        password = properties.getProperty("redis-password");
        
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(32);
        config.setMaxIdle(32);
        config.setMaxWaitMillis(2000L); // this prevents lockup on main thread, in the event pool is used on main thread
        source = new JedisPool(config, host, port, 5000, password);
        
        crosstalk = getConnection();
        subscribe();
    }
    
    private static String getNetworkType() {
        return ManiaCore.getInstance().getServerManager().getCurrentServer().getNetworkType().name();
    }
    
    public static void pushObject(String key, RedisObject object) {
        try (Jedis jedis = getConnection()) {
            jedis.hmset(key, object.serialize());
        }
    }
    
    public static RedisObject getObject(Class<? extends RedisObject> type, String key) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = jedis.hgetAll(key);
            Constructor<?> constructor = type.getConstructor(data.getClass());
            if (constructor != null) {
                return (RedisObject) constructor.newInstance(data);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Map<String, String> mapGet(String key) {
        try (Jedis jedis = getConnection()) {
            return jedis.hgetAll(key);
        }
    }
    
    public static void pushToggles(User user) {
        Map<String, String> data = new HashMap<>();
        for (Entry<Toggles, Toggle> entry : user.getToggles().entrySet()) {
            data.put(entry.getKey().name(), entry.getValue().getValue());
        }
        
        try (Jedis jedis = getConnection()) {
            jedis.hmset(getNetworkType() + "~TOGGLES-" + user.getUniqueId().toString(), data);
        }
    }
    
    public static Map<Toggles, Toggle> getToggles(UUID uuid) {
        Map<Toggles, Toggle> toggles = new HashMap<>();
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = jedis.hgetAll(getNetworkType() + "~TOGGLES-" + uuid.toString());
            for (Entry<String, String> entry : data.entrySet()) {
                Toggles type = Toggles.valueOf(entry.getKey());
                Toggle toggle = new Toggle(uuid, type.name().toLowerCase(), entry.getValue(), entry.getValue());
                toggles.put(type, toggle);
            }
        }
        return toggles;
    }
    
    public static void pushFriendship(Friendship friendship) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = new HashMap<>();
            data.put("id", friendship.getId() + "");
            data.put("player1", friendship.getPlayer1().toString());
            data.put("player2", friendship.getPlayer2().toString());
            data.put("timestamp", friendship.getTimestamp() + "");
            jedis.hmset(getNetworkType() + "~FRIENDSHIP:" + friendship.getId(), data);
        }
    }
    
    public static Friendship getFriendship(int id) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = jedis.hgetAll(getNetworkType() + "~FRIENDSHIP:" + id);
            if (!data.isEmpty()) {
                return new Friendship(data);
            }
        }
        return null;
    }
    
    public static List<Friendship> getFriendships() {
        List<Friendship> friendships = new ArrayList<>();
        try (Jedis jedis = getConnection()) {
            for (String key : jedis.keys(getNetworkType() + "~FRIENDSHIP:*")) {
                Map<String, String> data = jedis.hgetAll(getNetworkType() + "~FRIENDSHIP:" + key.split(":")[1]);
                if (!data.isEmpty()) {
                    friendships.add(new Friendship(data));
                }
            }
        }
        return friendships;
    }
    
    public static void pushFriendRequest(FriendRequest request) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = new HashMap<>();
            data.put("id", request.getId() + "");
            data.put("from", request.getSender().toString());
            data.put("to", request.getTo().toString());
            data.put("timestamp", request.getTimestamp() + "");
            jedis.hmset(getNetworkType() + "~FRIENDREQUEST:" + request.getId(), data);
        }
    }
    
    public static FriendRequest getFriendRequest(int id) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = jedis.hgetAll(getNetworkType() + "~FRIENDREQUEST:" + id);
            if (!data.isEmpty()) {
                return new FriendRequest(data);
            }
        }
        
        return null;
    }
    
    public static List<FriendRequest> getFriendRequests() {
        List<FriendRequest> friendRequests = new ArrayList<>();
        try (Jedis jedis = getConnection()) {
            for (String key : jedis.keys(getNetworkType() + "~FRIENDREQUEST:*")) {
                Map<String, String> data = jedis.hgetAll(getNetworkType() + "~FRIENDREQUEST:" + key.split(":")[1]);
                if (!data.isEmpty()) {
                    friendRequests.add(new FriendRequest(data));
                }
            }
        }
        
        return friendRequests;
    }
    
    public static void pushFriendNotification(FriendNotification notification) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = new HashMap<>();
            data.put("id", notification.getId() + "");
            data.put("sender", notification.getSender().toString());
            data.put("target", notification.getTarget().toString());
            data.put("type", notification.getType().name());
            data.put("timestamp", notification.getTimestamp() + "");
            jedis.hmset(getNetworkType() + "~FRIENDNOTIFICATION:" + notification.getId(), data);
        }
    }
    
    public static FriendNotification getFriendNotification(int id) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = jedis.hgetAll(getNetworkType() + "~FRIENDNOTIFICATION:" + id);
            if (!data.isEmpty()) {
                return new FriendNotification(data);
            }
        }
        return null;
    }
    
    public static List<FriendNotification> getFriendNotifications() {
        List<FriendNotification> friendNotifications = new ArrayList<>();
        try (Jedis jedis = getConnection()) {
            for (String key : jedis.keys(getNetworkType() + "~FRIENDNOTIFICATIONS:*")) {
                Map<String, String> data = jedis.hgetAll(getNetworkType() + "~FRIENDNOTIFICATION:" + key.split(":")[1]);
                if (!data.isEmpty()) {
                    friendNotifications.add(new FriendNotification(data));
                }
            }
        }
        return friendNotifications;
    }
    
    public static void pushUser(User user) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> data = new HashMap<>();
            data.put("id", user.getId() + "");
            data.put("uniqueId", user.getUniqueId().toString());
            data.put("name", user.getName());
            data.put("rank", user.getRank().name());
            jedis.hmset(getNetworkType() + "~USER:" + user.getUniqueId().toString(), data);
            pushToggles(user);
            addUUIDIDMapping(user.getUniqueId(), user.getId());
            addUUIDToNameMapping(user.getUniqueId(), user.getName());
            pushUserStats(user);
        }
    }
    
    public static void pushUserStats(User user) {
        Map<String, String> realStats = new HashMap<>();
        for (Entry<String, Statistic> entry : user.getStats().entrySet()) {
            String value = new Gson().toJson(entry.getValue());
            realStats.put(entry.getKey(), value);
        }
        
        if (!realStats.isEmpty()) {
            try (Jedis jedis = getConnection()) {
                jedis.hmset(getNetworkType() + "~UserStats:" + user.getUniqueId().toString(), realStats);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, String> fakedStats = new HashMap<>();
        for (Entry<String, Statistic> entry : user.getFakeStats().entrySet()) {
            String value = new Gson().toJson(entry.getValue());
            fakedStats.put(entry.getKey(), value);
        }

        if (!fakedStats.isEmpty()) {
            try (Jedis jedis = getConnection()) {
                jedis.hmset(getNetworkType() + "~UserFakedStats:" + user.getUniqueId().toString(), fakedStats);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static Map<String, Statistic> getUserStats(UUID uuid) {
        Map<String, Statistic> stats = new HashMap<>();
        try (Jedis jedis = getConnection()) {
            Map<String, String> rawData = jedis.hgetAll(getNetworkType() + "~UserStats:" + uuid.toString());
            for (Entry<String, String> entry : rawData.entrySet()) {
                Statistic statistic = new Gson().fromJson(entry.getValue(), Statistic.class);
                stats.put(entry.getKey(), statistic);
            }
        }
        return stats;
    }

    public static Map<String, Statistic> getUserFakedStats(UUID uuid) {
        Map<String, Statistic> stats = new HashMap<>();
        try (Jedis jedis = getConnection()) {
            Map<String, String> rawData = jedis.hgetAll(getNetworkType() + "~UserFakedStats:" + uuid.toString());
            for (Entry<String, String> entry : rawData.entrySet()) {
                Statistic statistic = new Gson().fromJson(entry.getValue(), Statistic.class);
                stats.put(entry.getKey(), statistic);
            }
        }
        return stats;
    }
    
    public static void deleteUserData(UUID uuid) {
        try (Jedis jedis = getConnection()) {
            jedis.del(getNetworkType() + "~USER:" + uuid.toString());
        }
    }
    
    public static void deleteUserStats(UUID uuid) {
        try (Jedis jedis = getConnection()) {
            jedis.del(getNetworkType() + "~UserStats:" + uuid.toString());
        }
    }
    
    public static void deleteToggles(UUID uuid) {
        try (Jedis jedis = getConnection()) {
            jedis.del(getNetworkType() + "~TOGGLES-" + uuid.toString());
        }
    }
    
    public static Map<String, String> getUserData(UUID uuid) {
        Map<String, String> data = new HashMap<>();
        try (Jedis jedis = getConnection()) {
            if (jedis.exists(getNetworkType() + "~USER:" + uuid.toString())) {
                data.putAll(jedis.hgetAll(getNetworkType() + "~USER:" + uuid));
            }
        }
        return data;
    }
    
    public static Map<UUID, Integer> getUUIDToIDMap() {
        Map<UUID, Integer> map;
        try (Jedis jedis = getConnection()) {
            map = new HashMap<>();
            if (jedis.exists(getNetworkType() + "~uuidtoidmap")) {
                Map<String, String> rawMap = jedis.hgetAll(getNetworkType() + "~uuidtoidmap");
                for (Entry<String, String> entry : rawMap.entrySet()) {
                    try {
                        map.put(UUID.fromString(entry.getKey()), Integer.parseInt(entry.getValue()));
                    } catch (Exception e) {}
                }
            }
        }
        return map;
    }
    
    public static void updateUUIDtoIDMap(Map<UUID, Integer> map) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> rawMap = new HashMap<>();
            for (Entry<UUID, Integer> entry : map.entrySet()) {
                rawMap.put(entry.getKey().toString(), entry.getValue() + "");
            }
            jedis.hmset(getNetworkType() + "~uuidtoidmap", rawMap);
        }
    }
    
    public static int getUserIdFromUUID(UUID uuid) {
        return getUUIDToIDMap().get(uuid);
    }
    
    public static void deleteFriendship(Friendship friendship) {
        try (Jedis jedis = getConnection()) {
            jedis.del(getNetworkType() + "~FRIENDSHIP:" + friendship.getId());
        }
    }
    
    public static void deleteFriendRequest(FriendRequest request) {
        try (Jedis jedis = getConnection()) {
            jedis.del(getNetworkType() + "~FRIENDREQUEST:" + request.getId());
        }
    }
    
    public Set<UUID> getUserUUIDsInRedis() {
        Set<UUID> users = new HashSet<>();
        try (Jedis jedis = getConnection()) {
            Set<String> keys = jedis.keys(getNetworkType() + "~User:*");
            for (String key : keys) {
                users.add(UUID.fromString(key.split("-")[1]));
            }
        }
        return users;
    }
    
    public static UUID getUUIDFromID(int id) {
        Map<UUID, Integer> uuidToIDMap = getUUIDToIDMap();
        if (uuidToIDMap.containsValue(id)) {
            for (Entry<UUID, Integer> entry : uuidToIDMap.entrySet()) {
                if (entry.getValue() == id) {
                    return entry.getKey();
                }
            }
        }
        
        return null;
    }
    
    public static void addUUIDIDMapping(UUID uuid, Integer id) {
        Map<UUID, Integer> map = getUUIDToIDMap();
        map.put(uuid, id);
        updateUUIDtoIDMap(map);
    }
    
    public static Map<UUID, String> getUUIDToNameMap() {
        Map<UUID, String> map;
        try (Jedis jedis = getConnection()) {
            map = new HashMap<>();
            if (jedis.exists(getNetworkType() + "~uuidtonamemap")) {
                Map<String, String> rawMap = jedis.hgetAll(getNetworkType() + "~uuidtonamemap");
                for (Entry<String, String> entry : rawMap.entrySet()) {
                    map.put(UUID.fromString(entry.getKey()), entry.getValue());
                }
            }
        }
        return map;
    }
    
    public static void updateUUIDtoNameMap(Map<UUID, String> map) {
        try (Jedis jedis = getConnection()) {
            Map<String, String> rawMap = new HashMap<>();
            for (Entry<UUID, String> entry : map.entrySet()) {
                rawMap.put(entry.getKey().toString(), entry.getValue());
            }
            jedis.hmset(getNetworkType() + "~uuidtonamemap", rawMap);
        }
    }
    
    public static String getNameFromUUID(UUID uuid) {
        return getUUIDToNameMap().get(uuid);
    }
    
    public static UUID getUUIDFromName(String name) {
        Map<UUID, String> uuidToNameMap = getUUIDToNameMap();
        if (uuidToNameMap.containsValue(name)) {
            for (Entry<UUID, String> entry : uuidToNameMap.entrySet()) {
                if (entry.getValue() == null) { continue; }
                if (entry.getValue().equalsIgnoreCase(name)) {
                    return entry.getKey();
                }
            }
        }
        
        return null;
    }
    
    public static void addUUIDToNameMapping(UUID uuid, String name) {
        Map<UUID, String> map = getUUIDToNameMap();
        map.put(uuid, name);
        updateUUIDtoNameMap(map);
    }
    
    public static Jedis getConnection() {
        return source.getResource();
    }
    
    public static void registerListener(RedisListener listener) {
        listeners.add(listener);
    }
    
    public static void sendCommand(String command) {
        sendCommand(command, true);
    }
    
    public static void sendCommand(String command, boolean async) {
        command = command.trim();
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        final String finalCommand = getNetworkType() + "~" + command;
        Runnable runnable = () -> {
            try (Jedis jedis = getConnection()) {
                jedis.publish(CROSSTALK_CHANNEL, finalCommand);
            }
        };
        if (async) {
            ManiaCore.getInstance().getPlugin().runTaskAsynchronously(runnable);
        } else {
            ManiaCore.getInstance().getPlugin().runTask(runnable);
        }
    }
    
    public static void subscribe() {
        if (subscriber != null && subscriber.isSubscribed()) { return; }
        ManiaCore.getInstance().getPlugin().runTaskAsynchronously(() -> crosstalk.subscribe(subscriber = new CommandSubscriber(), CROSSTALK_CHANNEL));
    }
    
    public static class CommandSubscriber extends JedisPubSub {
        
        @Override
        public void onMessage(String channel, String command) {
            try {
                String[] typeSplit = command.split("~");
                if (typeSplit != null && typeSplit.length == 2) {
                    if (getNetworkType().equalsIgnoreCase(NetworkType.valueOf(typeSplit[0]).name())) {
                        command = command.replace(getNetworkType() + "~", "");
                    } else {
                        return;
                    }
                }
            } catch (Exception e) {
                ManiaCore.getInstance().getLogger().severe("Error while parsing Redis command Network Type: " + e.getMessage());
                return;
            }
            String[] args = new String[]{};
            if (command.contains(" ")) {
                String[] wholeCommand = command.split(" ");
                if (wholeCommand.length > 1) {
                    List<String> argList = new ArrayList<>(Arrays.asList(wholeCommand).subList(1, wholeCommand.length));
                    args = argList.toArray(args);
                }
                command = wholeCommand[0];
            }
            final String finalCommand = command.trim();
            final String[] finalArgs = args;
            ManiaCore.getInstance().getPlugin().runTask(() -> {
                for (RedisListener listener : listeners) {
                    listener.onCommand(finalCommand, finalArgs);
                }
            });
        }
        
        @Override
        public void onPMessage(String s, String s1, String s2) {
            
        }
        
        @Override
        public void onSubscribe(String s, int i) {
            
        }
        
        @Override
        public void onUnsubscribe(String s, int i) {
            
        }
        
        @Override
        public void onPUnsubscribe(String s, int i) {
            
        }
        
        @Override
        public void onPSubscribe(String s, int i) {
            
        }
    }
}