package net.hungermania.manialib.multicraft.data;

public record ServerInfo(int memory, int startMemory, int port, int autoStart, int defaultLevel, int daemonId,
                         int announceSave, int kickDelay, int suspended, int autosave, int players, int id,
                         int diskQuota, String ip, String world, String jarfile,
                         String jardir, String template, String setup,
                         String prevJarfile, String params, String crashCheck,
                         String domain, String name,
                         String dir) implements MulticraftObject {
    
}