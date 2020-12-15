package net.hungermania.maniacore.api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hungermania.manialib.sql.Column;
import net.hungermania.manialib.sql.DataType;
import net.hungermania.manialib.sql.Row;

import java.util.LinkedList;
import java.util.List;

@Getter @AllArgsConstructor
public class NetworkLocation {
    private String server, worldName;
    private double x, y, z;
    private float yaw, pitch;
    
    public static List<Column> getColumnList(String prefix) {
        List<Column> columns = new LinkedList<>();
        columns.add(new Column(prefix + "_server", DataType.VARCHAR, 64, false, false));
        columns.add(new Column(prefix + "_worldName", DataType.VARCHAR, 64, false, false));
        columns.add(new Column(prefix + "_x", DataType.DOUBLE, false, false));
        columns.add(new Column(prefix + "_y", DataType.DOUBLE, false, false));
        columns.add(new Column(prefix + "_z", DataType.DOUBLE, false, false));
        columns.add(new Column(prefix + "_yaw", DataType.FLOAT, false, false));
        columns.add(new Column(prefix + "_pitch", DataType.FLOAT, false, false));
        return columns;
    }
    
    public NetworkLocation(Row row, String prefix) {
        this.server = row.getString(prefix + "_server");
        this.worldName = row.getString(prefix + "_worldName");
        this.x = row.getDouble(prefix + "_x");
        this.y = row.getDouble(prefix + "_y");
        this.z = row.getDouble(prefix + "_z");
        this.yaw = row.getFloat(prefix + "_yaw");
        this.pitch = row.getFloat(prefix + "_pitch");
    }
    
}
