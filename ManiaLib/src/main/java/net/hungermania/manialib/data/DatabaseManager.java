package net.hungermania.manialib.data;

import net.hungermania.manialib.data.annotations.ColumnInfo;
import net.hungermania.manialib.util.Utils;

import java.lang.reflect.Field;
import java.util.*;

public class DatabaseManager {
    
    private Map<String, MysqlDatabase> databases = new HashMap<>();
    private Set<MysqlTypeHandler<?>> typeHandlers = new HashSet<>();
    private Map<String, IRecord> recordRegistry = new HashMap<>();
    
    public void registerRecord(IRecord record) {
        Set<Field> fields = Utils.getClassFields(record.getClass());
        Map<String, Column> columns = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
            if (columnInfo.ignored()) {
                continue;
            }
            try {
                MysqlTypeHandler<?> handler = getHandler(field.get(record));
                columns.put(field.getName(), new Column(field.getName(), handler, columnInfo.length(), columnInfo.autoIncrement(), columnInfo.unique()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    
    public MysqlTypeHandler<?> getHandler(Object object) {
        for (MysqlTypeHandler<?> typeHandler : this.typeHandlers) {
            if (typeHandler.matchesType(object)) {
                return typeHandler;
            }
        }
        return null;
    }
}
