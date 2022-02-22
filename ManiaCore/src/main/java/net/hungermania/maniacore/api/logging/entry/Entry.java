package net.hungermania.maniacore.api.logging.entry;

import net.hungermania.manialib.data.model.IRecord;

public abstract class Entry implements IRecord {
    protected int id = -1;
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
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public String getServer() {
        return server;
    }
    
    public void setServer(String server) {
        this.server = server;
    }
}