package net.hungermania.maniacore.api.logging.entry;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.manialib.data.model.IRecord;

@Getter
public abstract class Entry implements IRecord {
    @Setter protected int id = -1;
    protected long date;
    protected String server;
    
    public Entry() {}
    
    public Entry(int id, long date, String server) {
        this.id = id;
        this.date = date;
        this.server = server;
    }
    
    public Entry(long date, String server) {
        this.date = date;
        this.server = server;
    }
}