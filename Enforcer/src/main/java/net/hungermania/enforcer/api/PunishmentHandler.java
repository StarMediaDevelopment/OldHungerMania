package net.hungermania.enforcer.api;

import net.hungermania.enforcer.api.punishment.Punishment;

public interface PunishmentHandler {

    void handlePunishment(Punishment punishment);
}
