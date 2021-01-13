package net.hungermania.manialib.redis;

import net.hungermania.manialib.data.DatabaseManager;
import net.hungermania.manialib.data.annotations.ColumnInfo;
import net.hungermania.manialib.data.handlers.DataTypeHandler;
import net.hungermania.manialib.data.model.IRecord;
import net.hungermania.manialib.util.Utils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

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
    
    public static void startRedis(Logger logger) {
        CROSSTALK_CHANNEL = "Crosstalk";
        
        properties = new Properties();
        propertiesFile = new File("./redis.properties");
        if (!propertiesFile.exists()) {
            logger.severe("No Redis connection info found, creating defaults");
            
            properties.setProperty("redis-host", "localhost");
            properties.setProperty("redis-port", "6379");
            properties.setProperty("redis-password", "");
            
            try {
                propertiesFile.createNewFile();
                
                FileOutputStream out = new FileOutputStream(propertiesFile);
                properties.store(out, "Redis Connection Info. Do not reproduce");
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
    
    public static void cacheRecord(IRecord record, DatabaseManager manager) {
        Set<Field> fields = Utils.getClassFields(record.getClass());
        Map<String, String> serialized = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
            if (columnInfo.ignored()) {
                continue;
            }
            
            try {
                DataTypeHandler<?> handler = manager.getHandler(field.getType());
                String value = handler.serializeRedis(field.get(record));
                if (value == null || value.equals("")) continue;
                serialized.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        try (Jedis jedis = getConnection()) {
            jedis.hmset(record.getClass().getName() + ":" + record.getId(), serialized);
        }
    }
    
    public static IRecord getCachedRecord(DatabaseManager manager, Class<? extends IRecord> recordClass, int id) {
        Set<Field> fields = Utils.getClassFields(recordClass);
        Map<String, String> recordData;
        try (Jedis jedis = getConnection()) {
            recordData = jedis.hgetAll(recordClass.getName() + ":" + id);
        }
        
        try {
            IRecord record = recordClass.getDeclaredConstructor().newInstance();

            for (Field field : fields) {
                field.setAccessible(true);
                ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
                if (columnInfo.ignored()) {
                    continue;
                }

                DataTypeHandler<?> handler = manager.getTableByRecordClass(recordClass).getColumn(field.getName()).getTypeHandler();
                Object value = handler.deserialize(recordData.get(field.getName()));
                field.set(record, value);
            }
        } catch (Exception e) {}
        return null;
    }
    
    public static Map<String, String> mapGet(String key) {
        try (Jedis jedis = getConnection()) {
            return jedis.hgetAll(key);
        }
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
        final String finalCommand = command;
        Runnable runnable = () -> {
            try (Jedis jedis = getConnection()) {
                jedis.publish(CROSSTALK_CHANNEL, finalCommand);
            }
        };
        runnable.run();
    }
    
    public static void subscribe() {
        if (subscriber != null && subscriber.isSubscribed()) { return; }
        new Thread(() -> crosstalk.subscribe(subscriber = new CommandSubscriber(), CROSSTALK_CHANNEL)).start();
    }
    
    public static class CommandSubscriber extends JedisPubSub {
        
        @Override
        public void onMessage(String channel, String command) {
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
            new Thread(() -> {
                for (RedisListener listener : listeners) {
                    listener.onCommand(finalCommand, finalArgs);
                }
            }).start();
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