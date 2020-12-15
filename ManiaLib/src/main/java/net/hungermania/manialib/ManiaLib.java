package net.hungermania.manialib;

import net.hungermania.manialib.sql.Database;

import java.util.Properties;
import java.util.logging.Logger;

public class ManiaLib {
    private Database database;
    private static ManiaLib instance;
    private Logger logger;
    
    public ManiaLib(Properties databaseProperties, Logger logger) {
        this.database = new Database(databaseProperties, logger);
        this.logger = logger;
        instance = this;
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
}