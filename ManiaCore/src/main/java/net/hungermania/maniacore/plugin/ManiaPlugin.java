package net.hungermania.maniacore.plugin;

import net.hungermania.manialib.data.model.DatabaseHandler;

public interface ManiaPlugin extends DatabaseHandler {
    String getVersion();
    String getName();
    
    default void setupDatabaseRecords() {}
    default void setupUserManager() {}
    default void setupRedisListeners() {}
    default void setupServerManager() {}
    
    ManiaTask runTask(Runnable runnable);
    ManiaTask runTaskAsynchronously(Runnable runnable);
    ManiaTask runTaskLater(Runnable runnable, long delay);
    ManiaTask runTaskLaterAsynchronously(Runnable runnable, long delay);
    ManiaTask runTaskTimer(Runnable runnable, long delay, long period);
    ManiaTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period);
}