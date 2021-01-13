package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.model.DataType;

public class StringHandler extends DataTypeHandler<String> {
    public StringHandler() {
        super(String.class, DataType.VARCHAR, 100);
    }

    public Object serializeSql(Object type) {
        return type;
    }

    public String deserialize(Object object) {
        return String.valueOf(object);
    }

    public String serializeRedis(Object object) {
        return (String) object;
    }
}
