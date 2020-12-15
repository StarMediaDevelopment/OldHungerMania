package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.events.EventInfo;
import net.hungermania.manialib.sql.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EventInfoRecord implements IRecord<EventInfo> {
    
    private EventInfo eventInfo;
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "events");
        table.addColumn(new Column("id", DataType.INT, true, true));
        table.addColumn(new Column("name", DataType.VARCHAR, 100));
        table.addColumn(new Column("active", DataType.VARCHAR, 5));
        table.addColumn(new Column("startTime", DataType.BIGINT));
        table.addColumn(new Column("settingsId", DataType.INT));
        table.addColumn(new Column("players", DataType.VARCHAR, 1000));
        table.addColumn(new Column("servers", DataType.VARCHAR, 1000));
        return table;
    }
    
    public EventInfoRecord(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }
    
    public EventInfoRecord(Row row) {
        int id = row.getInt("id");
        String name = row.getString("name");
        boolean active = row.getBoolean("active");
        long startTime = row.getLong("startTime");
        int settingsId = row.getInt("settingsId");
        String rawPlayers = row.getString("players");
        String rawServers = row.getString("servers");
        Set<Integer> players = new HashSet<>();
        Set<String> servers = new HashSet<>();
        if (!StringUtils.isEmpty(rawPlayers)) {
            for (String p : rawPlayers.split(",")) {
                players.add(Integer.parseInt(p));
            }
        }
        if (!StringUtils.isEmpty(rawServers)) {
            servers.addAll(Arrays.asList(rawServers.split(",")));
        }
        
        this.eventInfo = new EventInfo(id, name, active, startTime, settingsId, players, servers);
    }
    
    @Override
    public int getId() {
        return eventInfo.getId();
    }
    
    @Override
    public void setId(int id) {
        eventInfo.setId(id);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", eventInfo.getId());
            put("name", eventInfo.getName());
            put("active", eventInfo.isActive());
            put("startTime", eventInfo.getStartTime());
            put("settingsId", eventInfo.getSettingsId());
            put("players", StringUtils.join(eventInfo.getPlayers(), ","));
            put("servers", StringUtils.join(eventInfo.getServers(), ","));
        }};
    }
    
    @Override
    public EventInfo toObject() {
        return eventInfo;
    }
}