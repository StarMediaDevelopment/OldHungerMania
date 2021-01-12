package net.hungermania.manialib.data;

import net.hungermania.manialib.data.annotations.ColumnInfo;
import net.hungermania.manialib.data.annotations.TableInfo;
import net.hungermania.manialib.util.Utils;

import java.lang.reflect.Field;
import java.util.*;

public class DatabaseManager {
    
    private Map<String, MysqlDatabase> databases = new HashMap<>();
    private Set<MysqlTypeHandler<?>> typeHandlers = new HashSet<>();
    private Set<Class<? extends IRecord>> recordRegistry = new HashSet<>();
    private Set<Table> tableRegistry = new HashSet<>();
    
    public void registerRecord(IRecord record, MysqlDatabase database) {
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
                if (handler == null) {
                    System.out.println("Field " + field.getName() + " of the type " + record.getClass().getName() + " does not have a valid MysqlTypeHandler.");
                }
                columns.put(field.getName(), new Column(field.getName(), handler, columnInfo.length(), columnInfo.autoIncrement(), columnInfo.unique()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        String tableName = "";
        TableInfo tableInfo = record.getClass().getAnnotation(TableInfo.class);
        if (tableInfo != null) {
            if (!tableInfo.tableName().equals("")) {
                tableName = tableInfo.tableName();
            }
        } else {
            tableName = record.getClass().getSimpleName().toLowerCase();
        }
        
        Table table = new Table(tableName, columns.values());
        database.getTables().put(table.getName(), table);
        this.recordRegistry.add(record.getClass());
        this.tableRegistry.add(table);
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
