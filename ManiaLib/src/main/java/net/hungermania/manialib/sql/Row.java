package net.hungermania.manialib.sql;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.*;

public class Row {
    protected Map<String, Object> dataMap = new HashMap<>();
    protected Table table;
    
    public Row(Table table, ResultSet resultSet) {
        this.table = table;
        for (Column column : table.getColumns()) {
            try {
                Object object = resultSet.getObject(column.getName());
                this.dataMap.put(column.getName(), object);
            } catch (Exception throwables) {
                System.out.println("Error on getting column value " + column.getName() + " of table " + table.getName());
                throwables.printStackTrace();
            }
        }
    }
    
    public Map<String, Object> getDataMap() {
        return dataMap;
    }
    
    public int getInt(String key) {
        Object o = this.dataMap.get(key);
        try {
            if (o instanceof String) {
                return Integer.parseInt((String) o);
            }
        } catch (Exception e) {}
        if (o == null) {
            return 0;
        }
        return (int) o;
    }
    
    public IRecord getRecord() {
        Class<? extends IRecord> recordClass = table.getDatabase().getRecordClass(table);
        try {
            Constructor<?> constructor = recordClass.getDeclaredConstructor(Row.class);
            if (constructor == null) {
                return null;
            }
            return (IRecord) constructor.newInstance(this);
        } catch (Exception e) {
            System.out.println("An error occured loading a record for the class " + recordClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public String getString(String key) {
        return (String) this.dataMap.get(key);
    }
    
    public long getLong(String key) {
        Object o = this.dataMap.get(key);
        try {
            if (o instanceof String) {
                return Long.parseLong((String) o);
            }
        } catch (Exception e) {}
        if (o == null) {
            return 0;
        }
        return (long) o;
    }
    
    public boolean getBoolean(String key) {
        Object object = this.dataMap.get(key);
        if (object != null) {
            if (object instanceof String) {
                try {
                    return Boolean.parseBoolean(((String) object));
                } catch (Exception e) {
                    return false;
                }
            } else {
                return (boolean) this.dataMap.get(key);
            }
        } else {
            return false;
        }
    }
    
    public double getDouble(String key) {
        Object o = this.dataMap.get(key);
        try {
            if (o instanceof String) {
                return Double.parseDouble((String) o);
            }
        } catch (Exception e) {}
        if (o == null) {
            return 0;
        }
        return (double) o;
    }
    
    public float getFloat(String key) {
        Object o = this.dataMap.get(key);
        try {
            if (o instanceof String) {
                return Float.parseFloat((String) o);
            }
        } catch (Exception e) {}
        if (o == null) {
            return 0;
        }
        return (float) o;
    }
    
    public UUID getUUID(String key) {
        Object o = this.dataMap.get(key);
        try {
            if (o instanceof String) {
                return UUID.fromString((String) o);
            }
        } catch (Exception e) {}
        return (UUID) o;
    }
}