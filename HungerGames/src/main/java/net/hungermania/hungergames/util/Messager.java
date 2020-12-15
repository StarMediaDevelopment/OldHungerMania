package net.hungermania.hungergames.util;

import net.hungermania.maniacore.api.ranks.Rank;

/**
 * This class will control sending messages to groups of players.
 */
public abstract class Messager {
    
    public void sendMessage(String message) {
        sendMessage(message, null);
    }
    
    public abstract void sendMessage(String message, Rank rank);
}
