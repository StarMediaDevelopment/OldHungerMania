package net.hungermania.enforcer.api;

import lombok.Getter;
import lombok.Setter;
import net.hungermania.enforcer.api.punishment.Punishment;

import java.util.HashMap;
import java.util.Map;

public class PunishmentManager {

    @Getter @Setter private PunishmentHandler punishmentHandler;
    private Map<Integer, Punishment> punishments = new HashMap<>();

}
