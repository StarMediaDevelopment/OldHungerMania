package net.hungermania.maniacore.api;

import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.chat.ChatFormatter;
import net.hungermania.maniacore.api.chat.ChatManager;
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
import net.hungermania.maniacore.api.nickname.NicknameManager;
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
import net.hungermania.maniacore.api.util.ManiaProperties;
import net.hungermania.maniacore.memory.MemoryManager;
import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.manialib.ManiaLib;
import net.hungermania.manialib.data.DatabaseManager;
import net.hungermania.manialib.data.model.DatabaseHandler;
import net.hungermania.manialib.sql.Database;

import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import static net.hungermania.maniacore.api.chat.ChatFormatter.*;

public class ManiaCore implements DatabaseHandler {

    public static final Random RANDOM = new Random();

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
    private ChatManager chatManager;
    private NicknameManager nicknameManager;

    public void init(Logger logger, ManiaPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        Properties databaseProperties = new Properties();
        databaseProperties.setProperty("mysql-host", ManiaProperties.MYSQL_HOST);
        databaseProperties.setProperty("mysql-port", ManiaProperties.MYSQL_PORT + "");
        databaseProperties.setProperty("mysql-database", ManiaProperties.MYSQL_DATABASE);
        databaseProperties.setProperty("mysql-username", ManiaProperties.MYSQL_USERNAME);
        databaseProperties.setProperty("mysql-password", ManiaProperties.MYSQL_PASSWORD);
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
        this.database.registerRecordType(NicknameRecord.class);
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

        this.chatManager = new ChatManager();
        this.chatManager.setFormatter(Channel.GLOBAL, new ChatFormatter(LEVEL_FORMAT + " " + PLAYER_NAME_FORMAT + "&8: &r" + MESSAGE_FORMAT));
        ChatFormatter otherFormatter = new ChatFormatter(CHANNEL_HEADER + " " + "{truePrefix} {truerankbasecolor}{trueName}" + "&8: {truechatcolor}{message}");
        this.chatManager.setFormatter(Channel.STAFF, otherFormatter);
        this.chatManager.setFormatter(Channel.ADMIN, otherFormatter);

        this.nicknameManager = new NicknameManager();
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

    public ChatManager getChatManager() {
        return this.chatManager;
    }

    public NicknameManager getNicknameManager() {
        return nicknameManager;
    }
}