package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.DataType;
import net.hungermania.manialib.data.MysqlTypeHandler;

public class StringHandler extends MysqlTypeHandler<String> {
    public StringHandler() {
        super(String.class, DataType.VARCHAR);
    }

    public Object serialize(String type) {
        return type;
    }

    public String deserialize(Object object) {
        return String.valueOf(object);
    }
}
