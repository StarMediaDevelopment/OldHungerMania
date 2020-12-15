package net.hungermania.maniacore;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.RankRedisListener;
import net.hungermania.maniacore.api.records.StatRecord;
import net.hungermania.maniacore.api.records.UserRecord;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.bungee.cmd.*;
import net.hungermania.maniacore.bungee.communication.BungeeMessageHandler;
import net.hungermania.maniacore.bungee.listeners.BungeeListener;
import net.hungermania.maniacore.bungee.plugin.BungeeManiaTask;
import net.hungermania.maniacore.bungee.server.BungeeCordServerManager;
import net.hungermania.maniacore.bungee.user.*;
import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.maniacore.plugin.ManiaTask;
import net.hungermania.manialib.sql.Database;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.*;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class ManiaCoreProxy extends Plugin implements ManiaPlugin {
    
    private ManiaCore maniaCore;
    Configuration config;
    
    public void onEnable() {
        ManiaCore.setInstance(this.maniaCore = new ManiaCore());
        maniaCore.setLogger(getLogger());
        this.saveDefaultConfig();
        maniaCore.init(getLogger());
        maniaCore.setServerManager(new BungeeCordServerManager(maniaCore));
        maniaCore.getServerManager().init();
        getProxy().getPluginManager().registerListener(this, new BungeeListener(this));
        maniaCore.setUserManager(new BungeeUserManager(this));
        
        maniaCore.setPlugin(this);
        Redis.startRedis();
        Redis.registerListener(new RankRedisListener());
        Redis.registerListener(new UserRedisListener());
    
        try (Jedis jedis = Redis.getConnection()) {
            jedis.del("uuidtoidmap");
            jedis.del("uuidtonamemap");
        }
    
        getProxy().getPluginManager().registerCommand(this, new HubCommand());
        getProxy().getPluginManager().registerCommand(this, new DiscordCommand());
        //getProxy().getPluginManager().registerCommand(this, new EventsCommand(this));
        getProxy().getPluginManager().registerCommand(this, new GotoCmd());
        getProxy().getPluginManager().registerCommand(this, new RulesCommand());
        maniaCore.setMessageHandler(new BungeeMessageHandler());
        maniaCore.getMemoryManager().addManiaPlugin(this);
        
        
    }
    
    public void onDisable() {
        maniaCore.getDatabase().pushQueue();
    }
    
    public Database getDatabase() {
        return maniaCore.getDatabase();
    }
    
    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().severe("Could not reload the config.yml");
        }
    }
    
    public Configuration getConfig() {
        if (config == null) {
            saveDefaultConfig();
        }
        
        return config;
    }
    
    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (Exception e) {
            getLogger().severe("Could not save config.yml");
        }
    }
    
    public void saveDefaultConfig() {
        if (!getDataFolder().exists()) { getDataFolder().mkdir(); }
        
        File file = new File(getDataFolder(), "config.yml");
        
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        reloadConfig();
    }
    
    public ManiaCore getManiaCore() {
        return maniaCore;
    }
    
    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }
    
    @Override
    public String getName() {
        return getDescription().getName();
    }
    
    public ManiaTask runTask(Runnable runnable) {
        return new BungeeManiaTask(getProxy().getScheduler().schedule(this, runnable, 0, TimeUnit.MILLISECONDS));
    }
    
    public ManiaTask runTaskAsynchronously(Runnable runnable) {
        return new BungeeManiaTask(getProxy().getScheduler().runAsync(this, runnable));
    }
    
    public ManiaTask runTaskLater(Runnable runnable, long delay) {
        return new BungeeManiaTask(getProxy().getScheduler().schedule(this, runnable, delay * 50, TimeUnit.MILLISECONDS));
    }
    
    public ManiaTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return new BungeeManiaTask(getProxy().getScheduler().schedule(this, () -> getProxy().getScheduler().runAsync(ManiaCoreProxy.this, runnable), delay * 50, TimeUnit.MILLISECONDS));
    }
    
    public ManiaTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new BungeeManiaTask(getProxy().getScheduler().schedule(this, runnable, delay * 50, period * 50, TimeUnit.MILLISECONDS));
    }
    
    public ManiaTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new BungeeManiaTask(getProxy().getScheduler().schedule(this, () -> getProxy().getScheduler().runAsync(ManiaCoreProxy.this, runnable), delay * 50, period * 50, TimeUnit.MILLISECONDS));
    }
    
    public static void saveUserData(BungeeUser bungeeUser) {
        bungeeUser.setStats(Redis.getUserStats(bungeeUser.getUniqueId()));
        ManiaCore.getInstance().getDatabase().pushRecord(new UserRecord(bungeeUser));
        bungeeUser.getStats().forEach((type, stat) -> ManiaCore.getInstance().getDatabase().pushRecord(new StatRecord(stat)));
        if (!bungeeUser.isOnline()) {
            Redis.deleteUserData(bungeeUser.getUniqueId());
            Redis.deleteUserStats(bungeeUser.getUniqueId());
        }
    }
}