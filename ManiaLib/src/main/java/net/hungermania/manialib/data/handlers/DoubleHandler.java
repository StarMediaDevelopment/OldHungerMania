package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.DataType;
import net.hungermania.manialib.data.MysqlTypeHandler;

public class DoubleHandler extends MysqlTypeHandler<Double> {
    public DoubleHandler() {
        super(Double.class, DataType.DOUBLE);
    }

    public boolean matchesType(Object object) {
        return super.matchesType(object) || object.getClass().isAssignableFrom(double.class);
    }

    public Object serialize(Double object) {
        return object;
    }

    public Double deserialize(Object object) {
        Double value = null;
        if (object instanceof Double) {
            value = (Double) object;
        } else if (object instanceof String) {
            value = Double.parseDouble((String) object);
        }
        return value;
    }
}
