package net.hungermania.hungergames.lobby;

import net.hungermania.hungergames.util.Messager;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;

import java.util.HashSet;
import java.util.Set;

public class LobbyMessanger extends Messager {
    
    private Lobby lobby;
    
    public LobbyMessanger(Lobby lobby) {
        this.lobby = lobby;
    }
    
    public void sendMessage(String message, Rank permission) {
        Set<User> users = new HashSet<>(lobby.getPlayers());
        users.addAll(lobby.getHiddenStaff());
        for (User user : users) {
            if (permission != null) {
                if (user.hasPermission(permission)) {
                    user.sendMessage(message);
                } 
            } else {
                user.sendMessage(message);
            }
        }
    }
}
