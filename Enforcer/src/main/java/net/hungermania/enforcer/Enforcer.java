package net.hungermania.enforcer;

import lombok.Getter;
import net.hungermania.enforcer.api.PunishmentManager;
import net.hungermania.enforcer.api.records.PunishmentRecord;
import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.manialib.sql.Database;

@Getter
public class Enforcer {

    private ManiaCore maniaCore = ManiaCore.getInstance();
    private Database database = maniaCore.getDatabase();

    private PunishmentManager punishmentManager;

    public Enforcer() {
        database.registerRecordType(PunishmentRecord.class);
    }
}