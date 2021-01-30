package net.hungermania.maniacore.api.nickname;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.NicknameRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameManager {
    
    private Map<UUID, Nickname> nicknames = new HashMap<>();
    
    public void loadNicknames() {
        ManiaCore.getInstance().getDatabase().getRecords(NicknameRecord.class, null, null);
    }
}
