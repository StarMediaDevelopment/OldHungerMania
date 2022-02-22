package net.hungermania.maniacore.api.logging.entry;

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
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getSender() {
        return sender;
    }
    
    public void setSender(int sender) {
        this.sender = sender;
    }
}
