package net.hungermania.manialib.multicraft.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder @Getter @Setter
public class ServerInfo extends MulticraftObject {
    
    private int memory, startMemory, port, autoStart, defaultLevel, daemonId, announceSave, kickDelay, suspended, autosave, players, id, diskQuota;
    private String ip, world, jarfile, jardir, template, setup, prevJarfile, params, crashCheck, domain, name, dir;
}