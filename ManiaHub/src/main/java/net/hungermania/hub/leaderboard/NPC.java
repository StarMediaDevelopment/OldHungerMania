package net.hungermania.hub.leaderboard;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.skin.Skin;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class NPC {
    
    private UUID entityId;
    private int position;
    private EntityPlayer entity;
    private Skin skin;
    private Location location;
    private UUID uuid;
    
    public NPC(int position, EntityPlayer entity, Skin skin) {
        this.entity = entity;
        this.skin = skin;
        this.position = position;
        this.entityId = entity.getUniqueID();
        this.location = entity.getBukkitEntity().getLocation();
    }
    
    public NPC(int position, UUID entityId, Skin skin, Location location) {
        this.position = position;
        this.entityId = entityId;
        this.skin = skin;
        this.location = location;
    }
    
    public NPC(int position, Skin skin, Location location) {
        this.position = position;
        this.skin = skin;
        this.location = location;
    }
    
    public void send(Player player) {
        if (this.entity == null) {
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer worldServer = ((CraftWorld) Bukkit.getWorld("world")).getHandle();
            GameProfile gameProfile = new GameProfile(player.getUniqueId(), skin.getName());
            gameProfile.getProperties().clear();
            gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
            EntityPlayer npc = new EntityPlayer(server, worldServer, gameProfile, new PlayerInteractManager(worldServer));
            npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            this.entity = npc;
            this.entityId = npc.getUniqueID();
        }
    
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(this.entity);
        PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, this.entity);
        
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(playerInfo);
        connection.sendPacket(spawnPacket);
        
        DataWatcher dataWatcher = entity.getDataWatcher();
        dataWatcher.watch(10, (byte) 127);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entity.getId(), dataWatcher, true);
        connection.sendPacket(metadata);
    }
}
