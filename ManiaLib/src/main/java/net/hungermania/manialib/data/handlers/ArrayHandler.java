package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.DatabaseManager;
import net.hungermania.manialib.data.model.DataType;

import java.lang.reflect.Array;

public class ArrayHandler extends DataTypeHandler<Object[]> {
    public ArrayHandler() {
        super(Object[].class, DataType.VARCHAR, 1000);
    }

    public boolean matchesType(Class<?> clazz) {
        return clazz.isArray();
    }

    public Object serializeSql(Object object) {
        if (!object.getClass().isArray())
            return null;
        int length = Array.getLength(object);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            Object element = Array.get(object, i);
            sb.append(element.toString());

            if (i != length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public Object[] deserialize(Object object, Class<?> typeClass) {
        String[] elementValues = ((String) object).split(",");

        Object array = Array.newInstance(typeClass, elementValues.length);

        DataTypeHandler<?> typeHandler = DatabaseManager.getInstance().getHandler(typeClass);
        for (int i = 0; i < elementValues.length; i++) {
            Array.set(array, i, typeHandler.deserialize(elementValues[i], typeClass));
        }

        return (Object[]) array;
    }

    public String serializeRedis(Object object) {
        return null;
    }
}