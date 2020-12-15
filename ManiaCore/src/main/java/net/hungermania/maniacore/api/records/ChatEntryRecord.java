package net.hungermania.maniacore.api.records;

import net.hungermania.maniacore.api.logging.entry.ChatEntry;
import net.hungermania.manialib.sql.*;

import java.util.HashMap;
import java.util.Map;

public class ChatEntryRecord extends EntryRecord {
    
    public static Table generateTable(Database database) {
        Table table = EntryRecord.generateTable(database, "chat");
        Column sender = new Column("sender", DataType.INT, false, false);
        Column text = new Column("text", DataType.VARCHAR, 1000, false, false);
        Column channel = new Column("channel", DataType.VARCHAR, 32, false, false);
        table.addColumns(sender, text, channel);
        return table;
    }
    
    public ChatEntryRecord(ChatEntry entry) {
        super(entry);
    }
    
    public ChatEntryRecord(Row row) {
        super(row);
        int id = row.getInt("id");
        long date = row.getLong("date");
        String server = row.getString("server");
        int sender = row.getInt("sender");
        String text = row.getString("text");
        String channel = row.getString("channel");
        this.entry = new ChatEntry(id, date, server, sender, text, channel);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>(super.serialize()) {{
            ChatEntry chatEntry = (ChatEntry) toObject();
            put("sender", chatEntry.getSender());
            put("text", chatEntry.getText());
            put("channel", chatEntry.getChannel());
        }};
    }
}
