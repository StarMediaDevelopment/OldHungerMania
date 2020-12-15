package net.hungermania.maniacore.api.server;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ManiaServer {

    private int id;
    private String name;
    private int port;
    private ServerType type = ServerType.UNKNOWN;
    private int serverNumber = 1;

    public ManiaServer(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public ManiaServer(int id, String name, int port, ServerType type, int serverNumber) {
        this.id = id;
        this.name = name;
        this.port = port;
        this.type = type;
        this.serverNumber = serverNumber;
    }
}
