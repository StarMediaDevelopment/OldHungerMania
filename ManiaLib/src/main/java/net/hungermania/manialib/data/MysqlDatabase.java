package net.hungermania.manialib.data;

import lombok.Getter;
import net.hungermania.manialib.data.annotations.ColumnInfo;
import net.hungermania.manialib.data.annotations.TableInfo;
import net.hungermania.manialib.data.handlers.DataTypeHandler;
import net.hungermania.manialib.data.model.*;
import net.hungermania.manialib.sql.DataSource;
import net.hungermania.manialib.sql.Statements;
import net.hungermania.manialib.util.Utils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("DuplicatedCode")
public class MysqlDatabase {
    private static final String URL = "jdbc:mysql://{hostname}:{port}/{database}?useSSL=false";
    private Logger logger;

    private DataSource dataSource;

    private DatabaseManager databaseManager;

    private Set<IRecord> localCache = new HashSet<>();

    @Getter private Map<String, Table> tables = new HashMap<>();
    @Getter private String databaseName;

    public MysqlDatabase(Properties properties, Logger logger, DatabaseManager databaseManager) {
        this.logger = logger;
        this.databaseManager = databaseManager;
        String host = properties.getProperty("mysql-host");
        int port = Integer.parseInt(properties.getProperty("mysql-port"));
        databaseName = properties.getProperty("mysql-database") + "2"; //TODO Temporary
        System.out.println(databaseName);
        String username = properties.getProperty("mysql-username");
        String password = properties.getProperty("mysql-password");
        String url = URL.replace("{hostname}", host).replace("{port}", port + "").replace("{database}", databaseName);
        this.dataSource = new DataSource(url, username, password);
    }

    public <T extends IRecord> List<T> getRecords(Class<T> recordType, String columnName, Object value) {
        //System.out.println("Getting all records of the class " + recordType.getName() + " with column name " + columnName + " and the value " + value);
        List<T> records = new LinkedList<>();
        for (Table table : this.tables.values()) {
            String tableName = "";
            TableInfo tableInfo = recordType.getAnnotation(TableInfo.class);
            if (tableInfo != null) {
                tableName = tableInfo.tableName();
            }
            
            if (tableName.equals("")) {
                tableName = recordType.getSimpleName().toLowerCase();
            }
            if (table.getName().equalsIgnoreCase(tableName)) {
                String sql = "SELECT * FROM " + table.getName();
                if (columnName != null) {
                    Column column = table.getColumn(columnName);
                    if (column == null) {
                        continue;
                    }
                    sql += " WHERE `" + column.getName() + "` = '" + value + "'";
                }

                //System.out.println(sql);

                try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        Row row = new Row(table, resultSet);
                        records.add(row.getRecord(recordType, databaseManager));
                    }
                } catch (Exception e) {
                    //logger.severe("An error occured: " + e.getMessage());
                    //e.printStackTrace();
                }
            }
        }
        
        records.sort(Comparator.comparingInt(IRecord::getId));
        return records;
    }
    
    public <T extends IRecord> T getRecord(Class<T> recordType, String columnName, Object value) {
        //System.out.printf("Getting a single record of the class %s with column name %s and the value %s", recordType.getName(), columnName, value);
        return getRecords(recordType, columnName, value).get(0);
    }

    public void pushRecord(IRecord record) {
        Table table = databaseManager.getTableByRecordClass(record.getClass());
        if (table == null)
            return;
        Map<String, Object> serialized = new HashMap<>();
        Set<Field> fields = Utils.getClassFields(record.getClass());

        for (Field field : fields) {
            field.setAccessible(true);
            ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
            if (columnInfo != null) {
                if (columnInfo.ignored()) {
                    continue;
                }
            }

            if (field.getType().isAssignableFrom(IRecord.class)) {
                try {
                    pushRecord((IRecord) field.get(record));
                    continue;
                } catch (IllegalAccessException e) {}
            }
            
            if (field.getType().isAssignableFrom(Collection.class)) {
                try {
                    Collection collection = (Collection) field.get(record);
                    boolean collectionContainsRecord = false;
                    for (Object o : collection) {
                        if (o.getClass().isAssignableFrom(IRecord.class)) {
                            pushRecord((IRecord) o);
                            collectionContainsRecord = true;
                        }
                    }
                    if (collectionContainsRecord) continue;
                } catch (IllegalAccessException e) {}
            }

            DataTypeHandler<?> handler = table.getColumn(field.getName()).getTypeHandler();
            if (handler == null) {
                //System.out.printf("There is no DataTypeHandler for field %s in class %s%n", field.getName(), record.getClass().getName());
                continue;
            }
            try {
                serialized.put(field.getName(), handler.serializeSql(field.get(record)));
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
            }
        }

        Column unique = null;
        for (Column column : table.getColumns()) {
            if (column.isUnique()) {
                unique = column;
                break;
            }
        }

        String querySQL = null;
        Iterator<Map.Entry<String, Object>> iterator = serialized.entrySet().iterator();

        if (unique != null) {
            String where = Statements.WHERE.replace("{column}", unique.getName()).replace("{value}", serialized.get(unique.getName()) + "");
            String selectSql = Statements.SELECT.replace("{database}", this.databaseName).replace("{table}", table.getName()) + " " + where;

            try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement(); ResultSet resultSet = statement.executeQuery(selectSql)) {
                if (resultSet.next()) {
                    Row row = new Row(table, resultSet);
                    if (!row.getDataMap().isEmpty()) {
                        StringBuilder sb = new StringBuilder();

                        while (iterator.hasNext()) {
                            Map.Entry<String, Object> entry = iterator.next();
                            if (entry.getValue() != null) {
                                DataType type = databaseManager.getHandler(entry.getValue().getClass()).getMysqlType();
                                if (type == null) {
                                    continue;
                                }
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
                //System.out.println(selectSql);
                //e.printStackTrace();
            }

            if (querySQL != null && !querySQL.equals("")) {
                try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
                    statement.execute(querySQL);
                } catch (Exception e) {
                    //System.out.println(querySQL);
                    //e.printStackTrace();
                }
            }
        }

        if (querySQL == null || querySQL.equals("")) {
            StringBuilder colBuilder = new StringBuilder(), valueBuilder = new StringBuilder();
            Iterator<Column> columnIterator = table.getColumns().iterator();
            while (columnIterator.hasNext()) {
                Column column = columnIterator.next();
                if (column.isUnique()) {
                    continue;
                }
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
                if (affectedRows == 0) {
                    return;
                }
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setId(generatedKeys.getInt(1));
                    }
                }
            } catch (Exception e) {
                //System.out.println(querySQL);
                //e.printStackTrace();
            }
        }
    }

    public void generateTables() {
        for (Table table : this.tables.values()) {
            String sql = table.generateCreationStatement();

            try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {
                statement.execute(sql);
            } catch (Exception e) {
                //System.out.println(sql);
                //e.printStackTrace();
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
                            DataTypeHandler<?> handler = column.getTypeHandler();
                            if (handler.getMysqlType().equals(DataType.VARCHAR)) {
                                int length = handler.getDefaultLength();
                                if (column.getLength() > 0) {
                                    length = column.getLength();
                                }
                                columnType = handler.getMysqlType().name() + "(" + length + ")";
                            } else {
                                columnType = handler.getMysqlType().name();
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
                            } catch (Exception e) {
                                if (!e.getMessage().contains("Can't DROP")) {
                                    //System.out.println(columnSql);
                                    //System.out.println("database: " + databaseName);
                                    //e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (!e.getMessage().contains("Can't DROP")) {
                    //e.printStackTrace();
                }
            }
        }
    }
}