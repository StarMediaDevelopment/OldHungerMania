package net.hungermania.maniacore.api.data;

import net.hungermania.maniacore.api.util.Position;
import net.hungermania.manialib.data.handlers.DataTypeHandler;
import net.hungermania.manialib.data.model.DataType;

public class PositionTypeHandler extends DataTypeHandler<Position> {
    public PositionTypeHandler() {
        super(Position.class, DataType.VARCHAR, 1000);
    }

    public Object serializeSql(Object object) {
        String value = "";
        if (object instanceof Position) {
            Position position = (Position) object;
            StringBuilder sb = new StringBuilder();
            sb.append(position.getX()).append(",").append(position.getY()).append(",").append(position.getZ());
            if (position.getYaw() != 0) {
                sb.append(",").append(position.getYaw()).append(",").append(position.getPitch());
            }
            value = sb.toString();
        }
        return value;
    }

    public Position deserialize(Object object) {
        return null;
    }

    public String serializeRedis(Object object) {
        return null;
    }
}
