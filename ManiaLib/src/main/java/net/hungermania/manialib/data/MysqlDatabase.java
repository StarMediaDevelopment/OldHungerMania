package net.hungermania.manialib.data;

import net.hungermania.manialib.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

public class MysqlDatabase {
    private DataSource source;
    
    private Map<String, Table> tables = new HashMap<>();
    
}