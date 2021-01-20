package net.hungermania.maniacore.api.friends;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import lombok.*;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.pagination.IElement;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.manialib.data.model.IRecord;

import java.util.*;

@Getter
@AllArgsConstructor
@Builder
public class Friendship implements IElement, IRecord {
    
    @Setter
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
            status = "&aonline &eon &b" + ManiaCore.getInstance().getServerManager().getCurrentServer().getName();
        } else {
            PlayerObject playerObject = TimoCloudAPI.getUniversalAPI().getPlayer(other);
            if (playerObject != null) {
                if (playerObject.getServer() != null) {
                    status = "&aonline &eon &b" + playerObject.getServer().getName();
                }
            }
            if (status == null) {
                status = "&coffline";
            }
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
