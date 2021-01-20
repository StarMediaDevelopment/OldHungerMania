package net.hungermania.maniacore.api;

import net.hungermania.maniacore.api.communication.MessageHandler;
import net.hungermania.maniacore.api.events.EventManager;
import net.hungermania.maniacore.api.friends.FriendNotification;
import net.hungermania.maniacore.api.friends.FriendRequest;
import net.hungermania.maniacore.api.friends.FriendsManager;
import net.hungermania.maniacore.api.friends.Friendship;
import net.hungermania.maniacore.api.leveling.Level;
import net.hungermania.maniacore.api.leveling.LevelManager;
import net.hungermania.maniacore.api.logging.entry.ChatEntry;
import net.hungermania.maniacore.api.logging.entry.CmdEntry;
import net.hungermania.maniacore.api.records.*;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.server.ServerManager;
import net.hungermania.maniacore.api.skin.Skin;
import net.hungermania.maniacore.api.skin.SkinManager;
import net.hungermania.maniacore.api.stats.Statistic;
import net.hungermania.maniacore.api.user.IgnoreInfo;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.user.UserManager;
import net.hungermania.maniacore.api.user.toggle.Toggle;
import net.hungermania.maniacore.memory.MemoryManager;
import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.manialib.ManiaLib;
import net.hungermania.manialib.data.DatabaseManager;
import net.hungermania.manialib.data.model.DatabaseHandler;
import net.hungermania.manialib.sql.Database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

public class ManiaCore implements DatabaseHandler {
    
    public static final Random RANDOM = new Random();
    
    private File databasePropertiesFile;
    private Properties databaseProperties;
    
    private Database database;
    
    private ManiaLib maniaLib;
    private static ManiaCore instance;
    
    private DatabaseManager databaseManager;
    
    private ManiaPlugin plugin;
    
    private UserManager userManager;
    private MessageHandler messageHandler;
    private ServerManager serverManager;
    private SkinManager skinManager;
    private Logger logger;
    private LevelManager levelManager;
    private MemoryManager memoryManager;
    private EventManager eventManager;
    private FriendsManager friendsManager;
    
    public void init(Logger logger, ManiaPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        databasePropertiesFile = new File("./mania-mysql.properties");
        databaseProperties = new Properties();
        if (!databasePropertiesFile.exists()) {
            try {
                databasePropertiesFile.createNewFile();
                
                databaseProperties.setProperty("mysql-host", "localhost");
                databaseProperties.setProperty("mysql-port", "3306");
                databaseProperties.setProperty("mysql-database", "mania");
                databaseProperties.setProperty("mysql-username", "mania");
                databaseProperties.setProperty("mysql-password", "");
    
                FileOutputStream out = new FileOutputStream(databasePropertiesFile);
                databaseProperties.store(out, "HungerMania - Mysql Connection Info. Do not reproduce");
                out.close();
            } catch (IOException e) {
                logger.severe("Could not create the database properties file.");
            }
        } else {
            try (FileInputStream in = new FileInputStream(databasePropertiesFile)) {
                databaseProperties.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        this.maniaLib = new ManiaLib(databaseProperties, logger);
        this.database = maniaLib.getDatabase();
        this.databaseManager = maniaLib.getDatabaseManager();
        maniaLib.addDatabaseHandler(this);
        this.database.registerRecordType(UserRecord.class);
        this.database.registerRecordType(ChatEntryRecord.class);
        this.database.registerRecordType(CmdEntryRecord.class);
        this.database.registerRecordType(SkinRecord.class);
        this.database.registerRecordType(LevelRecord.class);
        this.database.registerRecordType(EventInfoRecord.class);
        this.database.registerRecordType(IgnoreInfoRecord.class);
        this.database.registerRecordType(StatRecord.class);
        this.database.registerRecordType(FriendRequestRecord.class);
        this.database.registerRecordType(FriendshipRecord.class);
        this.database.registerRecordType(FriendNotificationRecord.class);
        this.database.registerRecordType(ToggleRecord.class);
        plugin.setupDatabaseRecords();
        this.database.generateTables();
        
        plugin.setupUserManager();
        if (this.userManager == null) {
            getLogger().severe("No UserManager found!");
        }
        
        plugin.setupServerManager();

        Redis.startRedis();
        plugin.setupRedisListeners();
        
        this.skinManager = new SkinManager(this);
        skinManager.loadFromDatabase();
        this.levelManager = new LevelManager(this);
        this.levelManager.loadFromDatabase();
        this.levelManager.generateDefaults();
    
        this.memoryManager = new MemoryManager();
        this.eventManager = new EventManager(this);
        this.eventManager.loadData();
        
        this.friendsManager = new FriendsManager();
        plugin.runTaskLater(() -> maniaLib.init(), 1L);
    }

    public void registerRecordTypes() {
        this.databaseManager.registerRecordClasses(maniaLib.getMysqlDatabase(), User.class, ChatEntry.class, CmdEntry.class, Skin.class, Level.class, IgnoreInfo.class, Statistic.class, 
                FriendRequest.class, Friendship.class, FriendNotification.class, Toggle.class);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    public Database getDatabase() {
        return database;
    }
    
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
    
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        Redis.registerListener(messageHandler);
    }
    
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    
    public UserManager getUserManager() {
        return userManager;
    }
    
    public static ManiaCore getInstance() {
        return instance;
    }
    
    public static void setInstance(ManiaCore instance) {
        ManiaCore.instance = instance;
    }
    
    public ServerManager getServerManager() {
        return serverManager;
    }

    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        Redis.registerListener(serverManager);
    }

    public Logger getLogger() {
        return logger;
    }
    
    public SkinManager getSkinManager() {
        return skinManager;
    }
    
    public LevelManager getLevelManager() {
        return levelManager;
    }
    
    public MemoryManager getMemoryManager() {
        return memoryManager;
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    public void setPlugin(ManiaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public ManiaPlugin getPlugin() {
        return plugin;
    }
    
    public FriendsManager getFriendsManager() {
        return friendsManager;
    }
}