package net.hungermania.maniacore.api.events;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.maniacore.api.records.EventInfoRecord;
import net.hungermania.manialib.sql.IRecord;

import java.util.*;
import java.util.Map.Entry;

public class EventManager {
    
    private EventInfo activeEvent;
    private ManiaCore maniaCore;
    private Map<Integer, EventInfo> events = new HashMap<>();
    
    public EventManager(ManiaCore maniaCore) {
        this.maniaCore = maniaCore;
    }
    
    public void loadData() {
        List<IRecord> records = maniaCore.getDatabase().getRecords(EventInfoRecord.class, null, null);
        for (IRecord record : records) {
            if (record instanceof EventInfoRecord) {
                EventInfoRecord eventRecord = (EventInfoRecord) record;
                EventInfo eventInfo = eventRecord.toObject();
                if (eventInfo.isActive()) {
                    this.activeEvent = eventInfo;
                }
                this.events.put(eventInfo.getId(), eventInfo);
            }
        }
    }
    
    public Map<Integer, EventInfo> getEvents() {
        return events;
    }
    
    public EventInfo getActiveEvent() {
        return activeEvent;
    }
    
    public void saveData() {
        for (Entry<Integer, EventInfo> entry : this.events.entrySet()) {
            maniaCore.getDatabase().addRecordToQueue(new EventInfoRecord(entry.getValue()));
        }
        maniaCore.getDatabase().pushQueue();
    }
    
    public void setActiveEvent(EventInfo eventInfo) {
        this.activeEvent = eventInfo;
    }
}