package net.hungermania.maniacore.spigot.perks;

public enum PerkStatus {
    PURCHASED("&2"), PARTIALLY_PURCHASED("&a"), AVAILABLE("&e"), LOCKED("&c");
    
    private String color;
    
    PerkStatus(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
}
