package net.hungermania.manialib.sql;

public enum DBType {
    //
    MYSQL("jdbc:mysql://{hostname}:{port}/{database}?useSSL=false&serverTimezone=UTC", "com.mysql.jdbc.Driver"), SQLITE("jdbc:sqlite:{file}", "org.sqlite.JDBC");
    
    
    private String url, driverClass;
    
    DBType(String url, String driverClass) {
        this.url = url;
        this.driverClass = driverClass;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getDriverClass() {
        return driverClass;
    }
}