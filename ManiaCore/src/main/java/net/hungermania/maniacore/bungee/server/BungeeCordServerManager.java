package net.hungermania.maniacore.bungee.server;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.server.ManiaServer;
import net.hungermania.maniacore.api.server.ServerManager;
import net.hungermania.maniacore.api.server.ServerType;

public class BungeeCordServerManager extends ServerManager {
    public BungeeCordServerManager(ManiaCore maniaCore) {
        super(maniaCore);
    }

    @Override
    public void init() {
        this.currentServer = new ManiaServer("Proxy", 1); //TODO Find out how to get port
        this.currentServer.setType(ServerType.PROXY);
    }
    
    protected void handleServerStart(String server) {
        
    }
    
    protected void handleGameReady(String server) {
        
    }
    
    protected void handleServerStop(String server) {
        
    }
}
