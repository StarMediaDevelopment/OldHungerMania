package net.hungermania.manialib;

import lombok.Getter;
import net.hungermania.manialib.data.DatabaseManager;
import net.hungermania.manialib.data.MysqlDatabase;
import net.hungermania.manialib.data.model.DatabaseHandler;
import net.hungermania.manialib.sql.Database;

import java.util.Properties;
import java.util.logging.Logger;

public class ManiaLib {
    @Getter private MysqlDatabase mysqlDatabase;
    private Database database;
    private static ManiaLib instance;
    private Logger logger;
    
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    
    public ManiaLib(Properties databaseProperties, Logger logger) {
        this.database = new Database(databaseProperties, logger);
        this.databaseManager.registerDatabase(this.mysqlDatabase = new MysqlDatabase(databaseProperties, logger, databaseManager));
        this.logger = logger;
        instance = this;
    }
    
    public void addDatabaseHandler(DatabaseHandler databaseHandler) {
        this.databaseManager.addDatabaseHandler(databaseHandler);
    }
    
    public void init() {
        this.databaseManager.registerDatabases();
        this.databaseManager.registerTypeHandlers();
        this.databaseManager.registerRecordTypes();
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