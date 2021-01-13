package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.model.DataType;

public class IntegerHandler extends DataTypeHandler<Integer> {
    public IntegerHandler() {
        super(Integer.class, DataType.INT);
    }

    public boolean matchesType(Object object) {
        return super.matchesType(object) || object.getClass().isAssignableFrom(int.class);
    }

    public Object serializeSql(Object object) {
        return object;
    }

    public Integer deserialize(Object object) {
        Integer value = null;
        if (object instanceof Integer) {
            value = (Integer) object;
        } else if (object instanceof String) {
            value = Integer.parseInt((String) object);
        }
        return value;
    }

    public String serializeRedis(Object object) {
        return Integer.toString((int) object);
    }
}
