package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.DataType;
import net.hungermania.manialib.data.MysqlTypeHandler;

import java.util.UUID;

public class UUIDHandler extends MysqlTypeHandler<UUID> {

    public UUIDHandler() {
        super(UUID.class, DataType.VARCHAR);
    }

    public Object serialize(UUID object) {
        return object.toString();
    }

    public UUID deserialize(Object object) {
        if (object instanceof String) {
            return UUID.fromString((String) object);
        }
        return null;
    }
}
