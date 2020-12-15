package net.hungermania.maniacore.api.logging.entry;

import lombok.Getter;

@Getter
public class CmdEntry extends Entry {
    protected String text;
    protected int sender;
    
    public CmdEntry(int id, long date, String server, int sender, String text) {
        super(id, date, server);
        this.sender = sender;
        this.text = text;
    }
    
    public CmdEntry(long date, String server, int sender, String text) {
        super(date, server);
        this.sender = sender;
        this.text = text;
    }
}
