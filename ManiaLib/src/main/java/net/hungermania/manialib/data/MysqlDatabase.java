package net.hungermania.manialib.data;

import lombok.Getter;
import net.hungermania.manialib.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

public class MysqlDatabase {
    private DataSource source;
    
    @Getter private Map<String, Table> tables = new HashMap<>();
    
}