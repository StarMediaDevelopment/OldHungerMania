package net.hungermania.maniacore.api.stats;

public enum Stats implements Stat {
    
    HG_HIGHEST_KILL_STREAK("hg_highest_kill_streak", "0", true), HG_WINS("hg_wins", "0", true), HG_DEATHS("hg_deaths", "0", true), HG_MUTANT_KILLS("hg_mutant_kills", "0", true), HG_MUTANT_DEATHS("hg_mutant_deaths", "0", true), HG_DEATHMATCHES("hg_deathmatches", "0", true), HG_CHESTS_FOUND("hg_chests_found", "0", true), HG_GAMES("hg_games", "0", true), HG_KILLS("hg_kills", "0", true), HG_UNLOCKED_MUTATIONS("hg_unlocked_mutations", "PIG_ZOMBIE", false), HG_WINSTREAK("hg_winstreak", "0", true), HG_SCORE("hg_score", "100", true),
    
    COINS("coins", "0", true), EXPERIENCE("experience", "0", true), ONLINE_TIME("online_time", "0", true);
    
    static {
        for (Stats value : values()) {
            Stat.REGISTRY.put(value.getName(), value);
        }
    }
    
    private String name;
    private String defaultValue;
    private boolean number;
    
    Stats(String name, String defaultValue, boolean number) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.number = number;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public boolean isNumber() {
        return number;
    }
}
