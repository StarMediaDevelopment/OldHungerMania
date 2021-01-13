package net.hungermania.manialib.sql;

public final class Statements {
    private Statements() {}
    
    public static final String CREATE_DATABASE = "CREATE DATABASE {name};";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `{name}` ({columns}) ENGINE=InnoDB;";
    public static final String COLUMN_FORMAT = "`{colName}` {colType}";
    public static final String INSERT = "INSERT INTO {name} ({columns}) VALUES ({values});";
    public static final String UPDATE = "UPDATE {name} SET {values} WHERE {location}";
    public static final String UPDATE_VALUE = "`{column}`='{value}'";
    public static final String SELECT = "SELECT * FROM `{database}`.`{table}`";
    public static final String WHERE = "WHERE `{column}`='{value}'";
    public static final String DELETE = "DELETE FROM {name}";
    
    public static final String ALTER_TABLE = "ALTER TABLE {table} {logic};";
    public static final String ADD_COLUMN = "ADD COLUMN `{column}` {type}";
    public static final String DROP_COLUMN = "DROP COLUMN `{column}`";
    
    public static final String PRIMARY_COL = "`id` INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)";
}
