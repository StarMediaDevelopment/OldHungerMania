package net.hungermania.maniacore.api.user.toggle;

import net.hungermania.maniacore.api.ranks.Rank;

import java.util.UUID;

public enum Toggles {
    
    VANISHED("false"), PRIVATE_MESSAGES("true", "messages"), INCOGNITO("false"), STAFF_NOTIFICATIONS("true", "staff", Rank.HELPER), ADMIN_NOTIFICATIONS("true", "admin", Rank.ADMIN), SPARTAN_NOTIFICATIONS("true", "spartan", Rank.HELPER), FRIEND_REQUESTS("true", "requests");
    
    private String defaultValue, cmdName;
    private Rank rank = Rank.DEFAULT;
    
    Toggles(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    Toggles(String defaultValue, Rank rank) {
        this.defaultValue = defaultValue;
        this.rank = rank;
    }
    
    Toggles(String defaultValue, String cmdName) {
        this.defaultValue = defaultValue;
        this.cmdName = cmdName;
    }
    
    Toggles(String defaultValue, String cmdName, Rank rank) {
        this.defaultValue = defaultValue;
        this.cmdName = cmdName;
        this.rank = rank;
    }
    
    public String getCmdName() {
        return cmdName;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public Toggle create(UUID uuid) {
        return new Toggle(uuid, name().toLowerCase(), defaultValue, defaultValue);
    }
}
