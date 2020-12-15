package net.hungermania.maniacore.api.audit;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuditEntry {
    private int id;
    private long timestamp;
    private String oldValue, newValue, description;
}