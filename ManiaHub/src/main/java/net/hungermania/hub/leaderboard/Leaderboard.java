package net.hungermania.hub.leaderboard;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import net.hungermania.hub.ManiaHub;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.events.EventInfo;
import net.hungermania.maniacore.api.util.ManiaUtils;
import org.bukkit.Location;

import java.util.*;

public class Leaderboard {
    private Location location;
    private int min, max;
    private Hologram hologram;
    
    public Leaderboard(Location location, int min, int max) {
        this.location = location;
        this.min = min;
        this.max = max;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public int getMin() {
        return min;
    }
    
    public void setMin(int min) {
        this.min = min;
    }
    
    public int getMax() {
        return max;
    }
    
    public void setMax(int max) {
        this.max = max;
    }
    
    public Hologram getHologram() {
        return hologram;
    }
    
    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }
    
    public void spawn() {
        this.hologram = HologramsAPI.createHologram(ManiaHub.getInstance(), location);
        update();
    }
    
    public void update() {
        EventInfo activeEvent = ManiaCore.getInstance().getEventManager().getActiveEvent();
        if (activeEvent == null) {
            return;
        }
//        Map<UUID, HungerGamesProfile> profiles = new HashMap<>();
//        List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(ProfileRecord.class, null, null);
//        for (IRecord record : records) {
//            if (record instanceof ProfileRecord) {
//                HungerGamesProfile profile = ((ProfileRecord) record).toObject();
//                if (activeEvent.getPlayers().contains(profile.getUserId())) {
//                    profiles.put(profile.getUser().getUniqueId(), profile);
//                }
//            }
//        }
//        
//        Map<UUID, Integer> totalValues = new HashMap<>();
//        for (HungerGamesProfile profile : profiles.values()) {
//            totalValues.put(profile.getUser().getUniqueId(), profile.getScore());
//        }
        
        class LeaderboardPlayer implements Comparable<LeaderboardPlayer> {
            private UUID uuid;
            private int value;
            
            public LeaderboardPlayer(UUID uuid, int value) {
                this.uuid = uuid;
                this.value = value;
            }
            
            public UUID getUuid() {
                return uuid;
            }
            
            public void setUuid(UUID uuid) {
                this.uuid = uuid;
            }
            
            public int getValue() {
                return value;
            }
            
            public void setValue(int value) {
                this.value = value;
            }
            
            @Override
            public int compareTo(LeaderboardPlayer o) {
                return -Integer.compare(value, o.value);
            }
        }
        
        SortedSet<LeaderboardPlayer> sortedSet = new TreeSet<>();
//        for (Entry<UUID, Integer> entry : totalValues.entrySet()) {
//            sortedSet.add(new LeaderboardPlayer(entry.getKey(), entry.getValue()));
//        }
        
        
        SortedMap<Integer, UUID> rankedPlayers = new TreeMap<>();
        for (LeaderboardPlayer mapEntry : sortedSet) {
            UUID uuid = mapEntry.getUuid();
            try {
                if (rankedPlayers.lastKey() != null) {
                    rankedPlayers.put(rankedPlayers.lastKey() + 1, uuid);
                } else {
                    rankedPlayers.put(1, uuid);
                }
            } catch (Exception e) {
                rankedPlayers.put(1, uuid);
            }
        }
        
        Map<Integer, UUID> leaderboardPositions = new HashMap<>();
        
        for (int i = 0; i <= 12; i++) {
            if (rankedPlayers.containsKey(i + min)) {
                leaderboardPositions.put(i, rankedPlayers.get(i + min));
            }
        }
        
        HologramLine mainLine;
        try {
            mainLine = this.hologram.getLine(0);
            if (mainLine instanceof TextLine textLine) {
                if (!textLine.getText().contains("LEADERBOARD")) {
                    textLine.setText(ManiaUtils.color("&6&lLEADERBOARD (" + min + " - " + max + ")"));
                }
            }
        } catch (Exception e) {
            this.hologram.insertTextLine(0, ManiaUtils.color("&6&lLEADERBOARD (" + min + " - " + max + ")"));
        }

//        for (Entry<Integer, UUID> entry : leaderboardPositions.entrySet()) {
//            HologramLine line = null;
//            try {
//                line = this.hologram.getLine(entry.getKey() + 1);
//            } catch (Exception e) {}
//            HungerGamesProfile profile = profiles.get(entry.getValue());
//            User user = profile.getUser();
//            if (line != null) {
//                if (line instanceof TextLine) {
//                    TextLine textLine = (TextLine) line;
//                    String text = textLine.getText();
//                    if (text.contains(user.getName())) {
//                        if (!text.contains(profile.getScore() + "")) {
//                            textLine.setText(user.getName() + " : " + profile.getScore());
//                        }
//                    } else {
//                        textLine.setText(user.getName() + " : " + profile.getScore());
//                    }
//                }
//            } else {
//                this.hologram.insertTextLine(entry.getKey() + 1, user.getName() + " : " + profile.getScore());
//            }
//        }
    }
}