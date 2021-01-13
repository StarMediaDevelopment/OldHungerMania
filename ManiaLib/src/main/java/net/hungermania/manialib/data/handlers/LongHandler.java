package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.model.DataType;

public class LongHandler extends DataTypeHandler<Long> {
    public LongHandler() {
        super(Long.class, DataType.BIGINT);
    }

    @Override
    public boolean matchesType(Class<?> clazz) {
        System.out.println("Checking Long Type Handler");
        System.out.println("Class type " + clazz.getName());
        System.out.println("Super Matches: " + super.matchesType(clazz));
        System.out.println("Primitive Matches: " + clazz.isAssignableFrom(long.class));
        return super.matchesType(clazz) || clazz.isAssignableFrom(long.class);
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
