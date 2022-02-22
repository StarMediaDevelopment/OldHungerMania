package net.hungermania.maniacore.api.friends;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.pagination.IElement;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.manialib.data.model.IRecord;

import java.util.*;

public class Friendship implements IElement, IRecord {
    
    private int id;
    private UUID player1;
    private UUID player2;
    private long timestamp;
    
    public Friendship(UUID player1, UUID player2, long timestamp) {
        this.player1 = player1;
        this.player2 = player2;
        this.timestamp = timestamp;
    }
    
    public Friendship(Map<String, String> jedisData) {
        this.id = Integer.parseInt(jedisData.get("id"));
        this.player1 = UUID.fromString(jedisData.get("player1"));
        this.player2 = UUID.fromString(jedisData.get("player2"));
        this.timestamp = Long.parseLong(jedisData.get("timestamp"));
    }
    
    public Friendship(int id, UUID player1, UUID player2, long timestamp) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.timestamp = timestamp;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    public UUID getPlayer1() {
        return player1;
    }
    
    public void setPlayer1(UUID player1) {
        this.player1 = player1;
    }
    
    public UUID getPlayer2() {
        return player2;
    }
    
    public void setPlayer2(UUID player2) {
        this.player2 = player2;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String formatLine(String... args) {
        if (args.length != 1) { return null; }
        
        UUID cmdSender = UUID.fromString(args[0]);
        UUID other;
        if (this.player1.equals(cmdSender)) {
            other = player2;
        } else if (this.player2.equals(cmdSender)) {
            other = player1;
        } else {
            return null;
        }
        
        User otherUser = ManiaCore.getInstance().getUserManager().getUser(other);
        String status = null;
        if (otherUser.isOnline()) {
            //status = "&aonline &eon &b" + ManiaCore.getInstance().getServerManager().getCurrentServer().getName();
        } else {
//            PlayerObject playerObject = TimoCloudAPI.getUniversalAPI().getPlayer(other);
//            if (playerObject != null) {
//                if (playerObject.getServer() != null) {
//                    status = "&aonline &eon &b" + playerObject.getServer().getName();
//                }
//            }
//            if (status == null) {
//                status = "&coffline";
//            } //TODO
        }
        return otherUser.getColoredName() + " &eis " + status + "&e.";
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Friendship that = (Friendship) o;
        return Objects.equals(player1, that.player1) && Objects.equals(player2, that.player2);
    }
    
    public int hashCode() {
        return Objects.hash(player1, player2);
    }
}
