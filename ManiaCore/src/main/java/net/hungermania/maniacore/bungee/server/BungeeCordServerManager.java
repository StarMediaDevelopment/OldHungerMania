package net.hungermania.maniacore.bungee.server;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.server.*;

public class BungeeCordServerManager extends ServerManager {
    public BungeeCordServerManager(ManiaCore maniaCore) {
        super(maniaCore);
    }

    @Override
    public void init() {
        this.currentServer = new ManiaServer("Proxy", TimoCloudAPI.getBungeeAPI().getThisProxy().getPort());
        this.currentServer.setType(ServerType.PROXY);
    }
    
    protected void handleServerStart(String server) {
        
    }
    
    protected void handleGameReady(String server) {
        
    }
}
