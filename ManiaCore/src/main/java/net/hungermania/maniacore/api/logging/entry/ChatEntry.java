package net.hungermania.maniacore.api.logging.entry;

import lombok.Getter;

@Getter
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
}