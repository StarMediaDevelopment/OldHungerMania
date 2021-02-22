package net.hungermania.maniacore.api.server;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.redis.Redis;
import net.hungermania.maniacore.api.redis.RedisListener;
import net.hungermania.maniacore.api.util.ManiaProperties;

@SuppressWarnings("DuplicatedCode")
public abstract class ServerManager implements RedisListener {
    protected ManiaServer currentServer;
    protected ManiaCore maniaCore;
    protected NetworkType networkType = ManiaProperties.NETWORK_TYPE;
    
    public ServerManager(ManiaCore maniaCore) {
        this.maniaCore = maniaCore;
    }
    
    public abstract void init();
    
    public ManiaServer getCurrentServer() {
        return currentServer;
    }
    
    protected abstract void handleServerStart(String server);
    protected abstract void handleGameReady(String server);
    public void onCommand(String cmd, String[] args) {
        if (cmd.equals("serverStart")) {
            if (args.length != 1) return;
            String server = args[0];
            handleServerStart(server);
        } else if (cmd.equals("gameReady")) {
            if (args.length != 1) return;
            String server = args[0];
            handleGameReady(server);
        } else if (cmd.equals("serverStop")) {
            if (args.length != 1) {
                String server = args[0];
                handleServerStop(server);
            }
        }
    }
    
    protected abstract void handleServerStop(String server);
    
    public void sendServerStart(String server) {
        Redis.sendCommand("serverStart " + server);
    }
    
    public void sendServerStop(String server) {
//        try (Jedis jedis = Redis.getConnection()) {
//            jedis.publish(CROSSTALK_CHANNEL, "serverStop " + server);
//        }
    }
    
    public void sendGameReady(String server) {
        Redis.sendCommand("gameReady " + server);
    }
}