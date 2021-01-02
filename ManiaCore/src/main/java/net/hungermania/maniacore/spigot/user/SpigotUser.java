package net.hungermania.maniacore.spigot.user;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.api.util.ManiaUtils;
import net.hungermania.maniacore.spigot.perks.*;
import net.hungermania.manialib.sql.IRecord;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
            player.sendMessage(ManiaManiaUtils.color(s));
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
