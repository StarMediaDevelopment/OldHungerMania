package net.hungermania.manialib.multicraft.data;

import java.util.Set;

public record ServerStatus(int serverId, String status, int onlinePlayers, int maxPlayers,
                           Set<PlayerStatus> players) implements MulticraftObject {
}
