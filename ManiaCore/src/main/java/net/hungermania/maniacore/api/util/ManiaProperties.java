package net.hungermania.maniacore.api.util;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.server.NetworkType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ManiaProperties {
    
    private static File file;
    private static Properties properties;
    
    private static final ManiaProperties instance = new ManiaProperties();
    
    public static String MYSQL_HOST, MYSQL_PASSWORD, MYSQL_DATABASE, MYSQL_USERNAME, REDIS_HOST, REDIS_PASSWORD;
    public static int MYSQL_PORT, REDIS_PORT;
    public static NetworkType NETWORK_TYPE;

    public static ManiaProperties getInstance() {
        return instance;
    }

    public ManiaProperties() {
        file = new File("./mania.properties");
        properties = new Properties();
        if (!file.exists()) {
            try {
                file.createNewFile();
                createDefaultProperties();
            } catch (IOException e) {
                ManiaCore.getInstance().getLogger().severe("Could not create the mania.properties file!");
            }
        }
        
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (Exception e) {
            createDefaultProperties();
        }

        MYSQL_HOST = properties.getProperty("mysql-host");
        MYSQL_PASSWORD = properties.getProperty("mysql-password");
        MYSQL_DATABASE = properties.getProperty("mysql-database");
        MYSQL_USERNAME = properties.getProperty("mysql-username");
        REDIS_HOST = properties.getProperty("redis-host");
        REDIS_PASSWORD = properties.getProperty("redis-password");
        MYSQL_PORT = Integer.parseInt(properties.getProperty("mysql-port"));
        REDIS_PORT = Integer.parseInt(properties.getProperty("redis-port"));
        NETWORK_TYPE = NetworkType.valueOf(properties.getProperty("networkType").toUpperCase());
    }
    
    public void createDefaultProperties() {
        properties.setProperty("mysql-host", "localhost");
        properties.setProperty("mysql-password", "password");
        properties.setProperty("mysql-database", "database");
        properties.setProperty("mysql-username", "username");
        properties.setProperty("mysql-port", "3306");
        properties.setProperty("redis-host", "localhost");
        properties.setProperty("redis-port", "6379");
        properties.setProperty("redis-password", "");
        properties.setProperty("networkType", NetworkType.UNKNOWN.name());
        try (FileOutputStream out = new FileOutputStream(file)) {
            properties.store(out, "HungerMania - Network Configuration. Do not reproduce");
        } catch (Exception e) {}
        
    }
}
