package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.model.DataType;

public class LongHandler extends DataTypeHandler<Long> {
    public LongHandler() {
        super(Long.class, DataType.BIGINT);
    }

    public boolean matchesType(Object object) {
        return super.matchesType(object) || object.getClass().isAssignableFrom(long.class);
    }

    public Object serializeSql(Object object) {
        return object;
    }

    public Long deserialize(Object object) {
        Long value = null;
        if (object instanceof Long) {
            value = (Long) object;
        } else if (object instanceof String) {
            value = Long.parseLong((String) object);
        }
        return value;
    }

    public String serializeRedis(Object object) {
        return Long.toString((long) object);
    }
}
