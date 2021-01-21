package net.hungermania.manialib.data.model;

import net.hungermania.manialib.data.DatabaseManager;
import net.hungermania.manialib.data.annotations.ColumnInfo;
import net.hungermania.manialib.util.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Row {
    protected Map<String, Object> dataMap = new HashMap<>();
    protected Table table;
    
    public Row(Table table, ResultSet resultSet) {
        this.table = table;
        for (Column column : table.getColumns()) {
            try {
                this.dataMap.put(column.getName(), resultSet.getObject(column.getName())); //No longer deserializing here, just storing value from Database
            } catch (Exception throwables) {
                System.out.println("Error on getting column value " + column.getName() + " of table " + table.getName());
                throwables.printStackTrace();
            }
        }
    }
    
    public Map<String, Object> getDataMap() {
        return dataMap;
    }
    
    public <T extends IRecord> T getRecord(Class<T> recordClass, DatabaseManager databaseManager) {
        try {
            Table table = databaseManager.getTableByRecordClass(recordClass);
            Constructor<?> constructor = recordClass.getDeclaredConstructor();
            if (constructor == null) {
                return null;
            }
            constructor.setAccessible(true);
            T record = (T) constructor.newInstance();
            Set<Field> fields = Utils.getClassFields(recordClass);
            for (Field field : fields) {
                field.setAccessible(true);
                ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
                if (columnInfo != null) {
                    if (columnInfo.ignored())
                        continue;
                }
                String columnName = field.getName();
                Column column = table.getColumn(columnName);
                Object object = column.getTypeHandler().deserialize(this.dataMap.get(field.getName()), field.getType());
                field.set(record, object);
            }
            return record;
        } catch (Exception e) {
            System.out.println("An error occured loading a record for the class " + recordClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}