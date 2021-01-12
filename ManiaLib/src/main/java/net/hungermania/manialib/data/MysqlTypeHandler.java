package net.hungermania.manialib.data;

import lombok.Getter;

public abstract class MysqlTypeHandler<T> {
    
    @Getter protected Class<?> javaClass;
    @Getter protected DataType mysqlType;

    public MysqlTypeHandler(Class<?> javaClass, DataType mysqlType) {
        this.javaClass = javaClass;
        this.mysqlType = mysqlType;
    }
    
    protected MysqlTypeHandler() {}
    
    public boolean matchesType(Object object) {
       return object.getClass().isAssignableFrom(javaClass);
    }

    public abstract Object serialize(T object);
    public abstract T deserialize(Object object);
}
