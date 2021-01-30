package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.nickname.Nickname;
import net.hungermania.manialib.sql.Database;
import net.hungermania.manialib.sql.IRecord;
import net.hungermania.manialib.sql.Row;
import net.hungermania.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class NicknameRecord implements IRecord<Nickname> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "nicknames");
        
        
        return null;
    }
    
    private Nickname object;
    
    public NicknameRecord(Nickname nickname) {
        this.object = nickname;
    }
    
    public NicknameRecord(Row row) {
        
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            
        }};
    }
    
    public Nickname toObject() {
        return object;
    }
    
    public void setId(int id) {
        object.setId(id);
    }
    
    public int getId() {
        return object.getId();
    }
}
