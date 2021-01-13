package net.hungermania.manialib;

import net.hungermania.manialib.data.DatabaseManager;
import net.hungermania.manialib.sql.Database;

import java.util.Properties;
import java.util.logging.Logger;

public class ManiaLib {
    private Database database;
    private static ManiaLib instance;
    private Logger logger;
    
    private DatabaseManager databaseManager = new DatabaseManager();
    
    public ManiaLib(Properties databaseProperties, Logger logger) {
        this.database = new Database(databaseProperties, logger);
        this.logger = logger;
        instance = this;
    }
    
    public ManiaLib(Logger logger) {
        this.logger = logger;
    }
    
    public static ManiaLib getInstance() {
        return instance;
    }
    
    public Database getDatabase() {
        return database;
    }
    
    public Logger getLogger() {
        return logger;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}