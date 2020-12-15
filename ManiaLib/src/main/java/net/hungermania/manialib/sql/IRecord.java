package net.hungermania.manialib.sql;

import java.util.Map;
import java.util.function.Consumer;

/**
 * All record classes need a constructor that takes in a Row object that then maps the values
 * All classes also need a static Table generateTable(Database) method
 */
public interface IRecord<T> {
    
    int getId();
    void setId(int id);
    Map<String, Object> serialize();
    
    T toObject();
    
    default IRecord push(Database database) {
        database.pushRecord(this);
        return this;
    }
    
    default IRecord push(Database database, Consumer<IRecord> consumer) {
        database.pushRecord(this);
        consumer.accept(this);
        return this;
    }
}