package net.hungermania.maniacore.api.skin;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.SkinRecord;
import net.hungermania.manialib.sql.IRecord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class SkinManager {
    
    private ManiaCore maniaCore;
    
    private final Set<Skin> skins = new HashSet<>();
    
    public SkinManager(ManiaCore maniaCore) {
        this.maniaCore = maniaCore;
    }
    
    public void loadFromDatabase() {
        List<IRecord> records = maniaCore.getDatabase().getRecords(SkinRecord.class, null, null);
        for (IRecord record : records) {
            SkinRecord skinRecord = (SkinRecord) record;
            this.skins.add(skinRecord.toObject());
        }
    }
    
    public Skin getSkin(UUID uuid) {
        for (Skin skin : skins) {
            if (skin.getUuid().equals(uuid)) {
                return skin;
            }
        }
        
        return null;
    }
    
    public void getSkin(UUID uuid, Consumer<Skin> consumer) {
        Skin skin = getSkin(uuid);
        if (skin == null) {
            new Thread(() -> {
                Skin newSkin = new Skin(uuid);
                consumer.accept(newSkin);
                addSkin(newSkin);
            }).start();
        }
    }
    
    public synchronized void addSkin(Skin skin) {
        synchronized (this.skins) {
            for (Skin s : this.skins) {
                if (s.getUuid().equals(skin.getUuid())) {
                    return;
                }
            }
            this.skins.add(skin);
        }
    }
    
    public Set<Skin> getSkins() {
        return this.skins;
    }
}
