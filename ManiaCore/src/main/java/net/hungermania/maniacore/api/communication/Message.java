package net.hungermania.maniacore.api.communication;

import com.google.common.io.ByteArrayDataInput;

public class Message {
    private String channel, subChannel;
    private String id;
    private ByteArrayDataInput byteArray;
    
    public Message(String channel, String subChannel, String id, ByteArrayDataInput byteArray) {
        this.channel = channel;
        this.subChannel = subChannel;
        this.byteArray = byteArray;
        this.id = id;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public String getSubChannel() {
        return subChannel;
    }
    
    public String getId() {
        return id;
    }
    
    public ByteArrayDataInput getByteArray() {
        return byteArray;
    }
}
