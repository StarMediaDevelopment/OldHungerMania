package net.hungermania.maniacore.api.server;

public class ManiaServer {

    private int id;
    private String name;
    private int port;
    private ServerType type = ServerType.UNKNOWN;
    private int serverNumber = 1;
    private NetworkType networkType = NetworkType.UNKNOWN;

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
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public ServerType getType() {
        return type;
    }
    
    public void setType(ServerType type) {
        this.type = type;
    }
    
    public int getServerNumber() {
        return serverNumber;
    }
    
    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }
    
    public NetworkType getNetworkType() {
        return networkType;
    }
    
    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }
}
