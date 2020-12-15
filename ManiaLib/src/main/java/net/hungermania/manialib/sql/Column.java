package net.hungermania.manialib.sql;

public class Column {
    protected String name;
    protected DataType type;
    protected int length;
    protected boolean autoIncrement, unique;
    
    public Column(String name, DataType type, boolean autoIncrement, boolean unique) {
        this(name, type, 0, autoIncrement, unique);
    }
    
    public Column(String name, DataType type, int length, boolean autoIncrement, boolean unique) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.autoIncrement = autoIncrement;
        this.unique = unique;
    }
    
    public Column(String name, DataType type) {
        this(name, type, false, false);
    }
    
    public Column(String name, DataType type, int length) {
        this(name, type, length, false, false);
    }
    
    public void setLength(int length) {
        this.length = length;
    }
    
    public int getLength() {
        return length;
    }
    
    public String getCreationString() {
        StringBuilder sb = new StringBuilder("`");
        sb.append(name).append("` ").append(this.type.name());
        if (length > 0) {
            sb.append("(").append(length).append(")");
        }
        
        if (unique) {
            sb.append(" ").append("NOT NULL");
        }
        
        if (autoIncrement) {
            sb.append(" ").append("AUTO_INCREMENT");
        }
        
        if (unique) {
            sb.append(", PRIMARY KEY (`").append(this.name).append("`)");
        }
        
        return sb.toString();
    }
    
    public String getName() {
        return name;
    }
    
    public DataType getType() {
        return type;
    }
    
    public boolean isAutoIncrement() {
        return autoIncrement;
    }
    
    public boolean isUnique() {
        return unique;
    }
}