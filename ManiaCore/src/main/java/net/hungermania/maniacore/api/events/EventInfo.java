package net.hungermania.maniacore.api.events;

import java.util.HashSet;
import java.util.Set;

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
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public int getSettingsId() {
        return settingsId;
    }
    
    public void setSettingsId(int settingsId) {
        this.settingsId = settingsId;
    }
    
    public Set<Integer> getPlayers() {
        return players;
    }
    
    public void setPlayers(Set<Integer> players) {
        this.players = players;
    }
    
    public Set<String> getServers() {
        return servers;
    }
    
    public void setServers(Set<String> servers) {
        this.servers = servers;
    }
}