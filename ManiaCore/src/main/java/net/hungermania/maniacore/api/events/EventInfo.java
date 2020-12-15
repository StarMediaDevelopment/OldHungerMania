package net.hungermania.maniacore.api.events;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class EventInfo {
    
    private int id;
    private String name;
    private boolean active;
    private long startTime;
    private int settingsId;
    private Set<Integer> players = new HashSet<>();
    private Set<String> servers = new HashSet<>();
    
    public EventInfo(String name, long startTime) {
        this.name = name;
        this.startTime = startTime;
    }
    
    public EventInfo(int id, String name, boolean active, long startTime, int settingsId, Set<Integer> players, Set<String> servers) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.startTime = startTime;
        this.settingsId = settingsId;
        this.players = players;
        this.servers = servers;
    }
}