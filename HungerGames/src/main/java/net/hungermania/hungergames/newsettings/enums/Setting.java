package net.hungermania.hungergames.newsettings.enums;

import lombok.Getter;
import net.hungermania.hungergames.settings.Time;
import net.hungermania.hungergames.settings.Weather;
import net.hungermania.maniacore.api.util.Unit;

import static net.hungermania.hungergames.newsettings.GameSetting.Type;

public enum Setting {
    MAX_PLAYERS(Type.NUMBER, "24"),
    MIN_PLAYERS(Type.NUMBER, "6"),
    LOBBY_COUNTDOWN(Type.TIME, "45", Unit.SECONDS),
    MAX_HEALTH(Type.NUMBER, "20"),
    GRACE_PERIOD_LENGTH(Type.TIME, "60", Unit.SECONDS),
    MAX_GAMES(Type.NUMBER, "5"),
    GAME_LENGTH(Type.TIME, "20", Unit.MINUTES),
    MAX_MAP_OPTIONS(Type.NUMBER, "5"),
    DEATHMATCH_LENGTH(Type.TIME, "5", Unit.MINUTES),
    STARTING_COUNTDOWN(Type.TIME, "30", Unit.SECONDS),
    DM_AUTO_START(Type.NUMBER, "4"),
    PEARL_COOLDOWN(Type.TIME, "5", Unit.SECONDS),
    NEXT_GAME_START(Type.TIME, "10", Unit.SECONDS),
    MUTATIONS(Type.BOOLEAN, "true"),
    REGENERATION(Type.BOOLEAN, "true"),
    GRACE_PERIOD(Type.BOOLEAN, "true"),
    TIME_PROGRESSION(Type.BOOLEAN, "false"),
    WEATHER_PROGRESSION(Type.BOOLEAN, "false"),
    COIN_MULTIPLIER(Type.BOOLEAN, "true"),
    VOTE_WEIGHT(Type.BOOLEAN, "true"),
    TIME(Type.OTHER, Time.NOON.name()),
    WEATHER(Type.OTHER, Weather.CLEAR.name());

    @Getter private Type type;
    @Getter private String defaultValue;
    @Getter private Unit unit;

    Setting(Type type, String defaultValue, Unit unit) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.unit = unit;
    }

    Setting(Type type, String defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.unit = Unit.UNDEFINED;
    }
}