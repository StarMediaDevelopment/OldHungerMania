package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.model.DataType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumHandler extends DataTypeHandler<Enum> {
    public EnumHandler() {
        super(Enum.class, DataType.VARCHAR, 100);
    }

    public Object serializeSql(Object type) {
        if (type.getClass().isAssignableFrom(this.javaClass)) {
            try {
                Method nameMethod = type.getClass().getDeclaredMethod("name");
                return nameMethod.invoke(type);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return type;
    }

    public Enum deserialize(Object object) {
        if (object instanceof String) {
            try {
                Method valueOf = object.getClass().getDeclaredMethod("valueOf", String.class);
                return (Enum) valueOf.invoke(null, object);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null; //TODO
    }

    public String serializeRedis(Object object) {
        return (String) object;
    }
}
