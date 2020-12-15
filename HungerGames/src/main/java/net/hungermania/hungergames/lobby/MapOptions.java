package net.hungermania.hungergames.lobby;

import net.hungermania.hungergames.map.HGMap;
import net.hungermania.maniacore.api.ManiaCore;

import java.util.*;
import java.util.Map.Entry;

public class MapOptions {
    private SortedMap<Integer, HGMap> maps = new TreeMap<>();
    private Set<Vote> votes = new HashSet<>();
    
    public MapOptions() {}
    
    public void addMap(HGMap map) {
        int pos = 1;
        try {
            pos = maps.lastKey() + 1;
        } catch (Exception e) {}
        
        boolean hasMap = containsMap(map);
        
        if (hasMap) {
            return;
        }
        
        maps.put(pos, map);
    }
    
    public boolean containsMap(HGMap map) {
        for (HGMap hgMap : maps.values()) {
            if (hgMap.equals(map)) {
                return true;
            }
        }
        
        return false;
    }
    
    public VoteResult addVote(int map, UUID uuid, int weight) {
        for (Vote vote : votes) {
            if (vote.getUuid().equals(uuid)) {
                if (vote.getMap() != map) {
                    vote.setMap(map);
                    return VoteResult.CHANGED;
                } else {
                    return VoteResult.ALREADY_VOTED;
                }
            }
        }
        this.votes.add(new Vote(map, uuid, weight));
        return VoteResult.ADDED_VOTE;
    }
    
    public void clear() {
        this.maps.clear();
        this.votes.clear();
    }
    
    public Entry<HGMap, Integer> getMostVotedMap() {
        Map<HGMap, Integer> mapVotes = new HashMap<>();
        for (Vote vote : this.votes) {
            HGMap map = this.maps.get(vote.getMap());
            if (mapVotes.containsKey(map)) {
                mapVotes.replace(map, mapVotes.get(map) + vote.getWeight());
            } else {
                mapVotes.put(map, vote.getWeight());
            }
        }
        
        if (mapVotes.isEmpty()) {
            mapVotes.put(this.maps.get(ManiaCore.RANDOM.nextInt(this.maps.size() + 1)), 0);
        }
    
        Entry<HGMap, Integer> option = null;
        for (Entry<HGMap, Integer> entry : mapVotes.entrySet()) {
            if (option == null) {
                option = entry;
            } else {
                if (entry.getValue() > option.getValue()) {
                    option = entry;
                }
            }
        }
        return option;
    }
    
    public void removeVote(UUID uniqueId) {
        this.votes.removeIf(vote -> vote.getUuid().equals(uniqueId));
    }
    
    public int size() {
        return this.maps.size();
    }
    
    public SortedMap<Integer, HGMap> getMaps() {
        return this.maps;
    }
    
    public int getVotes(HGMap map) {
        int votes = 0;
        for (Vote vote : this.votes) {
            HGMap hgMap = this.maps.get(vote.getMap());
            if (map.equals(hgMap)) {
                votes += vote.getWeight();
            }
        }
        
        return votes;
    }
    
    public boolean hasVoted(UUID uniqueId) {
        for (Vote vote : votes) {
            if (vote.getUuid().equals(uniqueId)) {
                return true;
            }
        }
        return false;
    }
    
    public HGMap getVotedMap(UUID uniqueId) {
        for (Vote vote : votes) {
            if (vote.getUuid().equals(uniqueId)) {
                return this.maps.get(vote.getMap());
            }
        }
        
        return null;
    }
}
