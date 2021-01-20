package net.hungermania.maniacore;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.ranks.RankRedisListener;
import net.hungermania.maniacore.api.records.SkinRecord;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.skin.Skin;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.memory.MemoryHook;
import net.hungermania.maniacore.memory.MemoryHook.Task;
import net.hungermania.maniacore.plugin.ManiaPlugin;
import net.hungermania.maniacore.plugin.ManiaTask;
import net.hungermania.maniacore.spigot.anticheat.SpartanManager;
import net.hungermania.maniacore.spigot.cmd.*;
import net.hungermania.maniacore.spigot.communication.SpigotMessageHandler;
import net.hungermania.maniacore.spigot.perks.PerkInfo;
import net.hungermania.maniacore.spigot.perks.PerkInfoRecord;
import net.hungermania.maniacore.spigot.perks.Perks;
import net.hungermania.maniacore.spigot.plugin.SpigotManiaTask;
import net.hungermania.maniacore.spigot.server.SpigotServerManager;
import net.hungermania.maniacore.spigot.updater.Updater;
import net.hungermania.maniacore.spigot.user.FriendsRedisListener;
import net.hungermania.maniacore.spigot.user.SpigotUserManager;
import net.hungermania.manialib.ManiaLib;
import net.hungermania.manialib.sql.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class ManiaCorePlugin extends JavaPlugin implements Listener, ManiaPlugin {
    
    private ManiaCore maniaCore;
    
    private static final char[] CODE_CHARS = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    
    @Override
    public void onEnable() {
        ManiaCore.setInstance(this.maniaCore = new ManiaCore());
        maniaCore.init(getLogger(), this);
        maniaCore.setLogger(getLogger());
        this.saveDefaultConfig();
        runTask(() -> {
            //This makes sure that there is a user manager registered after the server has finished loading
            if (maniaCore.getUserManager() == null) {
                SpigotUserManager userManager = new SpigotUserManager(this);
                maniaCore.setUserManager(userManager);
            }
            getServer().getPluginManager().registerEvents((SpigotUserManager) maniaCore.getUserManager(), this);
        });
        
        getServer().getPluginManager().registerEvents(new SpartanManager(), this);
        
        getCommand("incognito").setExecutor(new IncognitoCmd(this));
        getCommand("broadcast").setExecutor(new BroadcastCmd());
        getCommand("say").setExecutor(new SayCmd());
        getCommand("memory").setExecutor(new MemoryCmd());
        getCommand("stats").setExecutor(new StatsCmd());
        getCommand("toggle").setExecutor(new ToggleCmd());
        getCommand("ignore").setExecutor(new IgnoreCmd());
        MsgCmd msgCmd = new MsgCmd();
        getCommand("message").setExecutor(msgCmd);
        getCommand("reply").setExecutor(msgCmd);
        getCommand("rank").setExecutor(new RankCmd());
        getCommand("friends").setExecutor(new FriendsCmd());
        getCommand("setstat").setExecutor(new SetstatCommand());
        getCommand("perks").setExecutor(new PerkCmd());
        getCommand("mutations").setExecutor(new MutationsCmd());
    
        new BukkitRunnable() {
            public void run() {
                getManiaDatabase().pushQueue();
            }
        }.runTaskTimerAsynchronously(this, 6000, 6000);
    
        maniaCore.setMessageHandler(new SpigotMessageHandler(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        
        MemoryHook playerUpdate = new MemoryHook("Core Player Update");
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(playerUpdate);
        new BukkitRunnable() {
            public void run() {
                Task task = playerUpdate.task().start();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getScoreboard() != null) {
                        Scoreboard scoreboard = player.getScoreboard();
                        
                        for (Rank rank : Rank.values()) {
                            boolean noTeam = true;
                            for (Team team : scoreboard.getTeams()) {
                                if (team.getName().equalsIgnoreCase(CODE_CHARS[rank.ordinal()] + "_" + rank.getName())) {
                                    noTeam = false;
                                }
                            }
                            if (noTeam) {
                                scoreboard.registerNewTeam(CODE_CHARS[rank.ordinal()] + "_" + rank.getName());
                            }
                        }
                        
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            User user = maniaCore.getUserManager().getUser(p.getUniqueId());
                            Rank rank = user.getRank();
                            Team team = scoreboard.getTeam(CODE_CHARS[rank.ordinal()] + "_" + rank.getName());
                            boolean existsInTeam = false;
                            if (team != null) {
                                for (String entry : team.getEntries()) {
                                    if (entry.equalsIgnoreCase(p.getName())) {
                                        existsInTeam = true;
                                        break;
                                    }
                                }
                            }
                            
                            for (Team t : scoreboard.getTeams()) {
                                if (t.getEntries().contains(p.getName())) {
                                    if (!t.getName().equalsIgnoreCase(CODE_CHARS[rank.ordinal()] + "_" + rank.getName())) {
                                        t.removeEntry(p.getName());
                                    }
                                }
                            }
                            
                            if (!existsInTeam) {
                                try {
                                    team.addEntry(p.getName());
                                } catch (Exception e) {}
                            }
                        }
                    }
                }
                task.end();
            }
        }.runTaskTimer(this, 20L, 20L);
        maniaCore.getMemoryManager().addManiaPlugin(this);
        
        this.runTaskLater(() -> maniaCore.getServerManager().sendServerStart(getManiaCore().getServerManager().getCurrentServer().getName()), 1L);
        this.runTaskTimer(new Updater(this), 1L, 1L);
        Perks.PERKS.size();
    }
    
    public ManiaCore getManiaCore() {
        return maniaCore;
    }

    public void registerRecordTypes() {
        ManiaLib.getInstance().getDatabaseManager().registerRecord(PerkInfo.class, ManiaLib.getInstance().getMysqlDatabase());
    }

    public void setupDatabaseRecords() {
        ManiaCore.getInstance().getDatabase().registerRecordType(PerkInfoRecord.class);
    }

    public void setupRedisListeners() {
        Redis.registerListener(new RankRedisListener());
        Redis.registerListener(new FriendsRedisListener());
    }

    public void setupUserManager() {
        maniaCore.setUserManager(new SpigotUserManager(this));
    }

    public void setupServerManager() {
        ManiaCore.getInstance().setServerManager(new SpigotServerManager(maniaCore));
        ManiaCore.getInstance().getServerManager().init();
    }

    @Override
    public void onDisable() {
        for (Skin skin : getManiaCore().getSkinManager().getSkins()) {
            maniaCore.getDatabase().addRecordToQueue(new SkinRecord(skin));
        }
    
        this.maniaCore.getDatabase().pushQueue();
        saveConfig();
    
        ManiaCore.getInstance().getServerManager().sendServerStop(getManiaCore().getServerManager().getCurrentServer().getName());
    }
    
    public Database getManiaDatabase() {
        return maniaCore.getDatabase();
    }
    
    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }
    
    public ManiaTask runTask(Runnable runnable) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTask(this, runnable));
    }
    
    public ManiaTask runTaskAsynchronously(Runnable runnable) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskAsynchronously(this, runnable));
    }
    
    public ManiaTask runTaskLater(Runnable runnable, long delay) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskLater(this, runnable, delay));
    }
    
    public ManiaTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskLaterAsynchronously(this, runnable, delay));
    }
    
    public ManiaTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period));
    }
    
    public ManiaTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period));
    }
}