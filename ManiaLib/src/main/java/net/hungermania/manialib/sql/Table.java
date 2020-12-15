package net.hungermania.manialib.sql;

import net.hungermania.manialib.collection.IncrementalMap;

import java.util.Collection;
import java.util.Iterator;

public class Table {
    protected String name;
    protected IncrementalMap<Column> columns = new IncrementalMap<>();
    protected Database database;
    
    public Table(Database database, String name) {
        this.name = name;
        this.database = database;
    }
    
    public String generateCreationStatement() {
        StringBuilder colBuilder = new StringBuilder();
        Iterator<Column> columnIterator = columns.values().iterator();
        while (columnIterator.hasNext()) {
            Column column = columnIterator.next();
            colBuilder.append(column.getCreationString());
            if (columnIterator.hasNext()) {
                colBuilder.append(",");
            }
        }
        
        return Statements.CREATE_TABLE.replace("{name}", name).replace("{columns}", colBuilder.toString());
    }
    
    public void addColumns(Column... columns) {
        for (Column column : columns) {
            this.columns.add(column);
        }
    }
    
    public Database getDatabase() {
        return database;
    }
    
    public String getName() {
        return name;
    }
    
    public Collection<Column> getColumns() {
        return columns.values();
    }
    
    public void addColumn(Column column) {
        this.columns.add(column);
    }
    
    public void addColumn(String name, DataType type, boolean autoIncrement, boolean unique) {
        addColumn(name, type, 0, autoIncrement, unique);
    }
    
    public void addColumn(String name, DataType type, int length, boolean autoIncrement, boolean unique) {
        this.columns.add(new Column(name, type, length, autoIncrement, unique));
    }
    
    public void addColumn(String name, DataType type) {
        addColumn(name, type, false, false);
    }
    
    public void addColumn(String name, DataType type, int length) {
        addColumn(name, type, length, false, false);
    } 
    
    public Column getColumn(String columnName) {
        for (Column column : columns.values()) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return column;
            }
        }
        return null;
    }
}