package net.hungermania.manialib.multicraft.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.Set;

@Builder @Getter @Setter
public class ServerStatus extends MulticraftObject {
    private int serverId;
    private String status;
    private int onlinePlayers, maxPlayers;
    @Singular private Set<PlayerStatus> players;
}
