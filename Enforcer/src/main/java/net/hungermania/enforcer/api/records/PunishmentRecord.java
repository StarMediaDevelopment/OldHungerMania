package net.hungermania.enforcer.api.records;

import net.hungermania.enforcer.api.punishment.Punishment;
import net.hungermania.manialib.sql.*;

import java.util.HashMap;
import java.util.Map;

import static net.hungermania.enforcer.api.punishment.Punishment.*;

public class PunishmentRecord implements IRecord<Punishment> {

    private Punishment object;

    public static Table generateTable(Database database) {
        Table table = new Table(database, "punishments");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("actor", DataType.VARCHAR, 50);
        table.addColumn("target", DataType.VARCHAR, 50);
        table.addColumn("reason", DataType.VARCHAR, 100);
        table.addColumn("pardonReason", DataType.VARCHAR, 100);
        table.addColumn("server", DataType.VARCHAR, 50);
        table.addColumn("type", DataType.VARCHAR, 50);
        table.addColumn("date", DataType.BIGINT);
        table.addColumn("pardonDate", DataType.BIGINT);
        table.addColumn("length", DataType.BIGINT);
        table.addColumn("visibility", DataType.VARCHAR, 50);
        table.addColumn("pardonVisibility", DataType.VARCHAR, 50);
        table.addColumn("active", DataType.VARCHAR, 5);
        table.addColumn("offline", DataType.VARCHAR, 5);
        table.addColumn("acknowledged", DataType.VARCHAR, 5);
        return table;
    }

    public PunishmentRecord(Punishment object) {
        this.object = object;
    }

    public PunishmentRecord(Row row) {
        this.object = builder().id(row.getInt("id")).actor(row.getString("actor")).target(row.getString("target")).reason(row.getString("reason"))
                .pardonReason(row.getString("pardonReason")).server(row.getString("server")).type(Type.valueOf(row.getString("type")))
                .date(row.getLong("date")).pardonDate(row.getLong("pardonDate")).length(row.getLong("length")).visibility(Visibility.valueOf(row.getString("visibility")))
                .pardonVisibility(Visibility.valueOf(row.getString("pardonVisibility"))).active(row.getBoolean("active")).acknowledged(row.getBoolean("acknowledged")).build();
    }

    public int getId() {
        return object.getId();
    }

    public void setId(int id) {
        object.setId(id);
    }

    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", object.getId());
            put("actor", object.getActor());
            put("target", object.getTarget());
            put("reason", object.getReason());
            put("pardonReason", object.getPardonReason());
            put("server", object.getServer());
            put("type", object.getType().name());
            put("date", object.getDate());
            put("pardonDate", object.getPardonDate());
            put("length", object.getLength());
            put("visibility", object.getVisibility().name());
            put("pardonVisibility", object.getPardonVisibility().name());
            put("active", object.isActive());
            put("offline", object.isOffline());
            put("acknowledged", object.isAcknowledged());
        }};
    }

    public Punishment toObject() {
        return object;
    }
}