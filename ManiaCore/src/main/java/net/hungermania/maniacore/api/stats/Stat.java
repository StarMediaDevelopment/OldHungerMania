package net.hungermania.maniacore.api.stats;

import java.util.*;

public interface Stat {
    
    Map<String, Stat> REGISTRY = new HashMap<>();
    
    String getName();
    String getDefaultValue();
    boolean isNumber();
    default Statistic create(UUID player) {
        return new Statistic(player, getName(), getDefaultValue(), System.currentTimeMillis(), System.currentTimeMillis());
    }
    
    static Stat getStat(String name) {
        return REGISTRY.get(name.toLowerCase().replace(" ", "_"));
    }
}
