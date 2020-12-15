package net.hungermania.manialib.multicraft.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ServerList extends MulticraftObject {
    private Map<Integer, String> servers = new HashMap<>();
    
    public void addServer(int id, String name) {
        this.servers.put(id, name);
    }
}
