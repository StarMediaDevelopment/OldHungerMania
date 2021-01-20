package net.hungermania.manialib.data.model;

public interface DatabaseHandler {
    
    default void registerDatabases() {}
    default void registerRecordTypes() {}
    default void registerTypeHandlers() {}
}
