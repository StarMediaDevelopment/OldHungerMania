package net.hungermania.maniacore.api.logging.entry;

public class ChatEntry extends Entry {
    protected String text, channel;
    protected int sender;
    
    public ChatEntry(int id, long date, String server, int sender, String text, String channel) {
        super(id, date, server);
        this.sender = sender;
        this.text = text;
        this.channel = channel;
    }
    
    public ChatEntry(long date, String server, int sender, String text, String channel) {
        super(date, server);
        this.sender = sender;
        this.text = text;
        this.channel = channel;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public int getSender() {
        return sender;
    }
    
    public void setSender(int sender) {
        this.sender = sender;
    }
}