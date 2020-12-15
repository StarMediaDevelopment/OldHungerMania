package net.hungermania.manialib.sql;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Database {
    private static final String URL = "jdbc:mysql://{hostname}:{port}/{database}?useSSL=false";
    private DataSource dataSource;
    
    private Map<String, Class<? extends IRecord>> tableToRecordMap = new HashMap<>();
    private Set<Table> tables = new HashSet<>();
    private Queue<IRecord> recordQueue = new LinkedBlockingQueue<>();
    private Set<IRecord> records = new HashSet<>();
    private String database;
    private Logger logger;
    
    public Database(Properties properties, Logger logger) {
        this.logger = logger;
        String host = properties.getProperty("mysql-host");
        int port = Integer.parseInt(properties.getProperty("mysql-port"));
        String database = properties.getProperty("mysql-database");
        String username = properties.getProperty("mysql-username");
        String password = properties.getProperty("mysql-password");
        String url = URL.replace("{hostname}", host).replace("{port}", port + "").replace("{database}", database);
        this.dataSource = new DataSource(url, username, password);
        this.database = database;
    }
    
    public boolean deleteRecord(IRecord record) {
        Table table = getRecordTable(record);
        if (table == null) return false;
    
        String where = Statements.WHERE.replace("{column}", "id").replace("{value}", record.getId() + "");
        String sql = Statements.DELETE.replace("{name}", table.getName() + " " + where);
        try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    private Table getRecordTable(IRecord record) {
        Table table = null;
        recordLoop:
        for (Entry<String, Class<? extends IRecord>> entry : this.tableToRecordMap.entrySet()) {
            if (entry.getValue().getName().equals(record.getClass().getName())) {
                for (Table t : tables) {
                    if (t.getName().equals(entry.getKey())) {
                        table = t;
                        break recordLoop;
                    }
                }
            }
        }
        
        return table;
    }
    
    public List<IRecord> getRecords(Class<? extends IRecord> recordType, String columnName, Object value) {
        List<IRecord> records = new ArrayList<>();
        for (Entry<String, Class<? extends IRecord>> entry : tableToRecordMap.entrySet()) {
            if (entry.getValue().equals(recordType)) {
                Table table = null;
                for (Table t : tables) {
                    if (t.getName().equalsIgnoreCase(entry.getKey())) {
                        table = t;
                    }
                }
                
                if (table == null) { continue; }
                
                String sql = "SELECT * FROM " + table.getName();
                if (columnName != null) {
                    Column column = table.getColumn(columnName);
                    if (column == null) { continue; }
                    sql += " WHERE " + column.getName() + " = '" + value + "'";
                }
    
                try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        Row row = new Row(table, resultSet);
                        records.add(row.getRecord());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.severe("An error occured: " + e.getMessage());
                }
            }
        }
        return records;
    }
    
    public void registerRecordType(Class<? extends IRecord> recordClass) {
        try {
            Method method = recordClass.getDeclaredMethod("generateTable", net.hungermania.manialib.sql.Database.class);
            Table table = (Table) method.invoke(null, this);
            registerTable(table);
            //generateTables();
            mapTableToRecord(table, recordClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SafeVarargs
    public final void registerRecordTypes(Class<? extends IRecord>... recordClasses) {
        for (Class<? extends IRecord> recordClass : recordClasses) {
            registerRecordType(recordClass);
        }
    }
    
    public void registerTable(Table table) {
        this.tables.add(table);
    }
    
    public void addRecordsToQueue(IRecord... records) {
        for (IRecord record : records) {
            addRecordToQueue(record);
        }
    }
    
    public void addRecordToQueue(IRecord record) {
        Map<String, Object> serialized = record.serialize();
        for (Object object : serialized.values()) {
            if (object instanceof IRecord) {
                addRecordToQueue((IRecord) object);
            }
        }
        
        this.recordQueue.add(record);
    }
    
    public void loadRecords() {
        for (Table table : tables) {
            String sql = "SELECT * FROM `" + table.getName() + "`;";
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    Row row = new Row(table, resultSet);
                    IRecord record = row.getRecord();
                    this.records.add(record);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.severe("An error occured: " + e.getMessage());
            }
        }
    }
    
    public Set<IRecord> getRecords() {
        return records;
    }
    
    public void pushRecord(IRecord record) {
        Table table = getRecordTable(record);
        if (table == null) return;
        Map<String, Object> serialized = record.serialize();
        Column unique = null;
        for (Column column : table.getColumns()) {
            if (column.isUnique()) {
                unique = column;
                break;
            }
        }
        
        String querySQL = null;
        Iterator<Entry<String, Object>> iterator = serialized.entrySet().iterator();
        
        if (unique != null) {
            String where = Statements.WHERE.replace("{column}", unique.getName()).replace("{value}", serialized.get(unique.getName()) + "");
            String selectSql = Statements.SELECT.replace("{database}", this.database).replace("{table}", table.getName()) + " " + where;
            
            try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement(); ResultSet resultSet = statement.executeQuery(selectSql)) {
                if (resultSet.next()) {
                    Row row = new Row(table, resultSet);
                    if (!row.getDataMap().isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        
                        while (iterator.hasNext()) {
                            Entry<String, Object> entry = iterator.next();
                            if (entry.getValue() != null) {
                                DataType type = DataType.getType(entry.getValue());
                                if (type == null) { continue; }
                                sb.append(Statements.UPDATE_VALUE.replace("{column}", entry.getKey()).replace("{value}", entry.getValue() + ""));
                                if (iterator.hasNext()) {
                                    sb.append(",");
                                }
                            }
                        }
                        
                        querySQL = Statements.UPDATE.replace("{values}", sb.toString()).replace("{location}", unique.getName() + "=" + serialized.get(unique.getName()));
                        querySQL = querySQL.replace("{name}", table.getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.severe("An error occured: " + e.getMessage());
            }
            
            if (!StringUtils.isEmpty(querySQL)) {
                try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
                    statement.execute(querySQL);
                } catch (Exception e) {
                    System.out.println(querySQL);
                    e.printStackTrace();
                    logger.severe("An error occured: " + e.getMessage());
                }
            }
        }
        
        if (StringUtils.isEmpty(querySQL)) {
            StringBuilder colBuilder = new StringBuilder(), valueBuilder = new StringBuilder();
            Iterator<Column> columnIterator = table.getColumns().iterator();
            while (columnIterator.hasNext()) {
                Column column = columnIterator.next();
                if (column.isUnique()) { continue; }
                colBuilder.append("`").append(column.getName()).append("`");
                valueBuilder.append("'").append(serialized.get(column.getName())).append("'");
                if (columnIterator.hasNext()) {
                    colBuilder.append(",");
                    valueBuilder.append(",");
                }
            }
            
            querySQL = Statements.INSERT.replace("{columns}", colBuilder.toString()).replace("{values}", valueBuilder.toString());
            querySQL = querySQL.replace("{name}", table.getName());
            
            try (Connection con = dataSource.getConnection(); PreparedStatement statement = con.prepareStatement(querySQL, Statement.RETURN_GENERATED_KEYS)) {
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) { return; }
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setId(generatedKeys.getInt(1));
                    }
                }
            } catch (Exception e) {
                System.out.println(querySQL);
                e.printStackTrace();
                logger.severe("An error occured: " + e.getMessage());
            }
        }
    }
    
    public void pushQueue() {
        while (!recordQueue.isEmpty()) {
            IRecord record = recordQueue.poll();
            if (record != null) {
                pushRecord(record);
            }
        }
    }
    
    public void mapTableToRecord(Table table, Class<? extends IRecord> record) {
        try {
            record.getDeclaredConstructor(Row.class);
        } catch (NoSuchMethodException e) {
            logger.severe("Record class " + record.getName() + " does not have a Constructor with the Row class as a parameter.");
            return;
        }
        
        this.tableToRecordMap.put(table.getName(), record);
    }
    
    public void generateTables() {
        for (Table table : this.tables) {
            String sql = table.generateCreationStatement();
            
            try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {
                statement.execute(sql);
            } catch (Exception e) {
                e.printStackTrace();
                logger.severe("An error occured: " + e.getMessage());
            }
            
            try (Connection con = dataSource.getConnection()) {
                DatabaseMetaData databaseMeta = con.getMetaData();
                try (ResultSet columns = databaseMeta.getColumns(null, null, table.getName(), null)) {
                    List<String> existingColumns = new ArrayList<>();
                    while (columns.next()) {
                        String name = columns.getString("COLUMN_NAME");
                        existingColumns.add(name);
                    }
    
                    List<String> columnSqls = new ArrayList<>();
                    for (Column column : table.getColumns()) {
                        if (!existingColumns.contains(column.getName())) {
                            String columnType;
                            if (column.getType().equals(DataType.VARCHAR)) {
                                columnType = column.getType().name() + "(" + column.getLength() + ")";
                            } else if (column.getType().equals(DataType.BOOLEAN)) {
                                columnType = column.getType().name() + "(5)";
                            } else {
                                columnType = column.getType().name();
                            }
                            String columnSql = Statements.ALTER_TABLE.replace("{table}", table.getName()).replace("{logic}", Statements.ADD_COLUMN.replace("{column}", column.getName()).replace("{type}", columnType));
                            columnSqls.add(columnSql);
                        }
                        existingColumns.remove(column.getName());
                    }
    
                    if (!existingColumns.isEmpty()) {
                        for (String existingColumn : existingColumns) {
                            String columnSql = Statements.ALTER_TABLE.replace("{table}", table.getName()).replace("{logic}", Statements.DROP_COLUMN.replace("{column}", existingColumn));
                            columnSqls.add(columnSql);
                        }
                    }
    
                    if (!columnSqls.isEmpty()) {
                        for (String columnSql : columnSqls) {
                            try (Statement statement = con.createStatement()) {
                                statement.executeUpdate(columnSql);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (!e.getMessage().contains("Can't DROP")) {
                    e.printStackTrace();
                    logger.severe("An error occured: " + e.getMessage());
                }
            }
        }
    }
    
    public Class<? extends IRecord> getRecordClass(Table table) {
        return this.tableToRecordMap.get(table.getName());
    }
    
    public void executeSql(String s) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(s);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}