package net.hungermania.hungergames.user;

import net.hungermania.hungergames.perks.*;
import net.hungermania.hungergames.records.PerkInfoRecord;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.channel.Channel;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.user.User;
import net.hungermania.maniacore.spigot.user.SpigotUser;
import net.hungermania.manialib.sql.IRecord;

import java.util.*;

public class GameUser extends SpigotUser {
    
    private Map<String, PerkInfo> perks = new HashMap<>();
    
    public GameUser(UUID uniqueId) {
        super(uniqueId);
    }
    
    public GameUser(UUID uniqueId, String name) {
        super(uniqueId, name);
    }
    
    public GameUser(Map<String, String> jedisData) {
        super(jedisData);
    }
    
    public GameUser(int id, UUID uniqueId, String name, long networkExperience, int coins, Rank rank, Channel channel, long onlineTime) {
        super(id, uniqueId, name, networkExperience, coins, rank, channel, onlineTime);
    }
    
    public GameUser(User user) {
        super(user);
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
