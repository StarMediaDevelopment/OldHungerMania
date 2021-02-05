package net.hungermania.maniacore.spigot.user;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.skin.Skin;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.perks.*;
import net.hungermania.manialib.sql.IRecord;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

@Getter @Setter
public class SpigotUser extends User {
    
    private PlayerBoard scoreboard;
    private Map<String, PerkInfo> perks = new HashMap<>();
    
    public SpigotUser(UUID uniqueId) {
        super(uniqueId);
    }
    
    public SpigotUser(UUID uniqueId, String name) {
        super(uniqueId, name);
    }
    
    public SpigotUser(Map<String, String> jedisData) {
        super(jedisData);
    }
    
    public SpigotUser(User user) {
        this(user.getId(), user.getUniqueId(), user.getName(), user.getRank(), user.getChannel());
    }
    
    public SpigotUser(int id, UUID uniqueId, String name, Rank rank, Channel channel) {
        super(id, uniqueId, name, rank, channel);
    }
    
    public void sendMessage(BaseComponent baseComponent) {
        getBukkitPlayer().spigot().sendMessage(baseComponent);
    }
    
    public void sendMessage(String s) {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.sendMessage(ManiaUtils.color(s));
        }
    }
    
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }
    
    public boolean hasPermission(String permission) {
        Player player = getBukkitPlayer();
        if (player != null) {
            if (permission == null || permission.equals("")) {
                return true;
            }
            
            return player.hasPermission(permission);
        }
        return false;
    }
    
    public boolean isOnline() {
        return getBukkitPlayer() != null;
    }

    public void applyNickname() {
        CraftPlayer craftPlayer = (CraftPlayer) this.getBukkitPlayer();
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        GameProfile gameProfile = entityPlayer.getProfile();

        Skin skin = ManiaCore.getInstance().getSkinManager().getSkin(nickname.getSkinUUID());
        PropertyMap properties = gameProfile.getProperties();
        properties.clear();
        properties.put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        try {
            Field nameField = gameProfile.getClass().getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(gameProfile, nickname.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        int dim = entityPlayer.getWorld().worldProvider.getDimension();
        EnumDifficulty diff = entityPlayer.getWorld().getDifficulty();
        WorldType type = entityPlayer.getWorld().worldData.getType();
        WorldSettings.EnumGamemode gamemode = WorldSettings.EnumGamemode.valueOf(craftPlayer.getGameMode().name());
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(dim, diff, type, gamemode);
        entityPlayer.playerConnection.sendPacket(respawn);
        
        List<Player> canSee = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.canSee(getBukkitPlayer())) {
                canSee.add(player);
                player.hidePlayer(getBukkitPlayer());
            }
        }

        for (Player player : canSee) {
            player.showPlayer(getBukkitPlayer());
        }
    }

    public void loadPerks() {
        List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(PerkInfoRecord.class, "uuid", this.uniqueId.toString());
        for (IRecord record : records) {
            if (record instanceof PerkInfoRecord) {
                PerkInfoRecord perkInfoRecord = (PerkInfoRecord) record;
                addPerkInfo(perkInfoRecord.toObject());
            }
        }
        
        for (Perk perk : Perks.PERKS) {
            if (!this.perks.containsKey(perk.getName())) {
                PerkInfo value = perk.create(this.uniqueId);
                this.perks.put(perk.getName(), value);
            }
        }
    }
    
    public PerkInfo getPerkInfo(Perk perk) {
        return perks.get(perk.getName());
    }
    
    public void addPerk(Perk perk) {
        this.perks.put(perk.getName(), perk.create(this.uniqueId));
    }
    
    public void addPerkInfo(PerkInfo perkInfo) {
        this.perks.put(perkInfo.getName(), perkInfo);
    }
    
    public Collection<PerkInfo> getPerks() {
        return this.perks.values();
    }
}
