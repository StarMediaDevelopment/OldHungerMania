package net.hungermania.manialib.sql;

public enum DataType {
    VARCHAR(String.class), 
    FLOAT(float.class), 
    INT(int.class), 
    BIGINT(long.class), 
    BOOLEAN(boolean.class), 
    DOUBLE(double.class);
    
    private final Class<?> clazz;
    
    DataType(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public Class<?> getClazz() {
        return clazz;
    }
    
    public static DataType getType(Object object) {
        for (DataType type : values()) {
            if (object.getClass().equals(type.getClazz())) {
                return type;
            }
        }
        
        return VARCHAR;
    }
}