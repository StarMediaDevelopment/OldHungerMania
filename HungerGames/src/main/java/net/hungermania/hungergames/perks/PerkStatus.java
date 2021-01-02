package net.hungermania.hungergames.perks;

import lombok.Getter;

public enum PerkStatus {
    PURCHASED("&2"), PARTIALLY_PURCHASED("&a"), AVAILABLE("&e"), LOCKED("&c");
    
    @Getter private String color;
    PerkStatus(String color) {
        this.color = color;
    }
}
