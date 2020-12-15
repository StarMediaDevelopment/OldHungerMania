package net.hungermania.maniacore.api.channel;

public enum Channel {
    GLOBAL, STAFF("STAFF", "&3", "&b", true), ADMIN("ADMIN", "&c", "&4", true);
    
    private String prefix, color, symbolColor;
    private boolean crossServer;
    Channel() {
        this.prefix = "";
        this.color = "&f";
        symbolColor = "&f";
        crossServer = false;
    }
    
    Channel(String prefix, String color, String symbolColor, boolean crossServer) {
        this.prefix = prefix;
        this.color = color;
        this.symbolColor = symbolColor;
        this.crossServer = crossServer;
    }
    
    public String getChatPrefix() {
        return getSymbolColor() + "&l[" + getColor() + getPrefix() + getSymbolColor() + "&l] " + getColor();
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getColor() {
        return color;
    }
    
    public String getSymbolColor() {
        return symbolColor;
    }
    
    public boolean isCrossServer() {
        return crossServer;
    }
    
    public String getPermission() {
        return "mania.channels." + name().toLowerCase();
    }
}
