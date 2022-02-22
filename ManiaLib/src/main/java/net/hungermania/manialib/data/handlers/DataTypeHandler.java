package net.hungermania.manialib.data.handlers;

import net.hungermania.manialib.data.model.DataType;

public abstract class DataTypeHandler<T> {
    
    protected Class<?> javaClass;
    protected DataType mysqlType;
    protected int defaultLength;

    public DataTypeHandler(Class<?> javaClass, DataType mysqlType) {
        this.javaClass = javaClass;
        this.mysqlType = mysqlType;
    }

    public DataTypeHandler(Class<?> javaClass, DataType mysqlType, int defaultLength) {
        this(javaClass, mysqlType);
        this.defaultLength = defaultLength;
    }

    protected DataTypeHandler() {
    }

    public boolean matchesType(Class<?> clazz) {
        return clazz.isAssignableFrom(javaClass);
    }

    public abstract Object serializeSql(Object object);

    public T deserialize(Object object, Class<?> typeClass) {
        return deserialize(object);
    }

    public T deserialize(Object object) {
        return null;
    }

    public abstract String serializeRedis(Object object);
    
    public Class<?> getJavaClass() {
        return javaClass;
    }
    
    public DataType getMysqlType() {
        return mysqlType;
    }
    
    public int getDefaultLength() {
        return defaultLength;
    }
}
