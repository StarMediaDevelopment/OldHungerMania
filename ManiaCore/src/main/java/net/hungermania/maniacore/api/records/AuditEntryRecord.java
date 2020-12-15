package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.audit.AuditEntry;
import net.hungermania.manialib.sql.*;

import java.util.HashMap;
import java.util.Map;

public class AuditEntryRecord implements IRecord<AuditEntry> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "auditentry");
        
        return table;
    }
    
    private AuditEntry object;
    
    public AuditEntryRecord(AuditEntry object) {
        this.object = object;
    }
    
    public AuditEntryRecord(Row row) {
        
    }
    
    public int getId() {
        return object.getId();
    }
    
    public void setId(int id) {
        object.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", object.getId());
        }};
    }
    
    public AuditEntry toObject() {
        return object;
    }
}