package net.hungermania.maniacore.api.nickname;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.ranks.Rank;
import net.hungermania.maniacore.api.records.NicknameRecord;
import net.hungermania.maniacore.api.user.User;

import java.util.*;

public class NicknameManager {
    
    private Map<UUID, Nickname> nicknames = new HashMap<>();
    
    private Set<String> blacklistedNames = new HashSet<>();
    
    public void loadNicknames() {
        ManiaCore.getInstance().getDatabase().getRecords(NicknameRecord.class, null, null);
    }
    
    public boolean isBlacklisted(String name) {
        User user = ManiaCore.getInstance().getUserManager().getUser(name);
        if (user != null) {
            if (user.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
                return true;
            }
        }

        for (String blname : this.blacklistedNames) {
            if (name.equalsIgnoreCase(blname)) {
                return true;
            }
        }
        
        return false;
    }
}
