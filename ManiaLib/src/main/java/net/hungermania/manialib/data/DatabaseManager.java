package net.hungermania.manialib.data;

import net.hungermania.manialib.data.annotations.ColumnInfo;
import net.hungermania.manialib.data.annotations.TableInfo;
import net.hungermania.manialib.data.exceptions.AlreadyRegisteredException;
import net.hungermania.manialib.data.handlers.*;
import net.hungermania.manialib.data.model.Column;
import net.hungermania.manialib.data.model.DatabaseHandler;
import net.hungermania.manialib.data.model.IRecord;
import net.hungermania.manialib.data.model.Table;
import net.hungermania.manialib.util.Utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DatabaseManager {

    private final static DatabaseManager instance = new DatabaseManager();
    
    public static DatabaseManager getInstance() {
        return instance;
    }
    
    private Map<String, MysqlDatabase> databases = new HashMap<>();
    private Set<DataTypeHandler<?>> typeHandlers = new HashSet<>();
    private Set<Class<? extends IRecord>> recordRegistry = new HashSet<>();
    private Set<Table> tableRegistry = new HashSet<>();
    private Set<DatabaseHandler> databaseHandlers = new HashSet<>();

    private boolean registeredTypeHandlers = false, registeredDatabases = false, registeredRecordTypes = false;

    private DatabaseManager() {
    }

    public void registerTypeHandlers() {
        try {
            registerTypeHandler(new BooleanHandler());
            registerTypeHandler(new DoubleHandler());
            registerTypeHandler(new IntegerHandler());
            registerTypeHandler(new LongHandler());
            registerTypeHandler(new StringHandler());
            registerTypeHandler(new UUIDHandler());
            registerTypeHandler(new ArrayHandler());
        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }

        for (DatabaseHandler databaseHandler : this.databaseHandlers) {
            databaseHandler.registerTypeHandlers();
        }
        
        this.registeredTypeHandlers = true;
    }
    
    public void registerDatabases() {
        for (DatabaseHandler databaseHandler : this.databaseHandlers) {
            databaseHandler.registerDatabases();
        }
        
        this.registeredDatabases = true;
    }
    
    public void registerRecordTypes() {
        for (DatabaseHandler databaseHandler : this.databaseHandlers) {
            databaseHandler.registerRecordTypes();
        }

        for (MysqlDatabase value : this.databases.values()) {
            value.generateTables();
        }
        
        this.registeredRecordTypes = true;
    }

    public Class<? extends IRecord> getRecordClassByTable(Table table) {
        for (Class<? extends IRecord> recordClass : recordRegistry) {
            TableInfo tableInfo = recordClass.getAnnotation(TableInfo.class);
            if (tableInfo != null) {
                if (!tableInfo.tableName().equals("")) {
                    if (tableInfo.tableName().equalsIgnoreCase(table.getName())) {
                        return recordClass;
                    }
                }
            } else {
                if (recordClass.getSimpleName().equalsIgnoreCase(table.getName())) {
                    return recordClass;
                }
            }
        }
        return null;
    }

    public Table getTableByRecordClass(Class<? extends IRecord> recordClass) {
        String tableName = null;
        TableInfo tableInfo = recordClass.getAnnotation(TableInfo.class);
        if (tableInfo != null) {
            if (!tableInfo.tableName().equals("")) {
                tableName = tableInfo.tableName();
            }
        }

        if (tableName == null) {
            tableName = recordClass.getSimpleName();
        }

        for (Table table : this.tableRegistry) {
            if (table.getName().equalsIgnoreCase(tableName)) {
                return table;
            }
        }

        return null;
    }

    public void registerTypeHandler(DataTypeHandler<?> handler) throws AlreadyRegisteredException {
        for (DataTypeHandler<?> typeHandler : this.typeHandlers) {
            if (typeHandler.getJavaClass().equals(handler.getJavaClass())) {
                throw new AlreadyRegisteredException("Class " + handler.getJavaClass().getName() + " is already being handled by " + typeHandler.getClass().getName());
            }
        }

        this.typeHandlers.add(handler);
    }
    
    public void registerRecordClasses(MysqlDatabase database, Class<? extends IRecord>... recordClasses) {
        if (recordClasses != null) {
            for (Class<? extends IRecord> recordClass : recordClasses) {
                registerRecord(recordClass, database);
            }
        }
    }

    public void registerRecord(Class<? extends IRecord> recordClass, MysqlDatabase database) {
        Table table = getTableByRecordClass(recordClass);
        if (table == null) {
            try {
                recordClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                //System.out.println("Could not find a default constructor for record " + recordClass.getName());
                return;
            }

            try {
                Field id = recordClass.getDeclaredField("id");
                if (!(id.getType().isAssignableFrom(int.class) || id.getType().isAssignableFrom(long.class))) {
                    //System.out.println("The ID field is not of type int or long");
                    return;
                }
            } catch (NoSuchFieldException e) {
                //System.out.println("Could not find an id field in the record " + recordClass.getName());
                return;
            }

            Set<Field> fields = Utils.getClassFields(recordClass);
            Map<String, Column> columns = new HashMap<>();
            for (Field field : fields) {
                field.setAccessible(true);
                ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
                if (columnInfo != null) {
                    if (columnInfo.ignored()) {
                        continue;
                    }
                }

                DataTypeHandler<?> handler;
                String colName = field.getName();
                int colLength = 0;
                boolean colAutoIncrement = false, colUnique = false;

                if (columnInfo != null) {
                    colLength = columnInfo.length();
                    colAutoIncrement = columnInfo.autoIncrement();
                    colUnique = columnInfo.unique();
                }

                handler = getHandler(field.getType());
                if (handler == null) {
                    //System.out.println("Field " + field.getName() + " which has the type " + field.getType() + " of the record " + recordClass.getName() + " does not have a valid MysqlTypeHandler.");
                    return;
                }

                if (field.getName().equalsIgnoreCase("id")) {
                    if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(long.class)) {
                        colAutoIncrement = true;
                        colUnique = true;
                    }
                }

                columns.put(field.getName(), new Column(colName, handler, colLength, colAutoIncrement, colUnique));
            }

            String tableName = "";
            TableInfo tableInfo = recordClass.getAnnotation(TableInfo.class);
            if (tableInfo != null) {
                if (!tableInfo.tableName().equals("")) {
                    tableName = tableInfo.tableName();
                }
            } else {
                tableName = recordClass.getSimpleName().toLowerCase();
            }
            table = new Table(tableName, columns.values());
        }
        database.getTables().put(table.getName(), table);
        this.recordRegistry.add(recordClass);
        this.tableRegistry.add(table);
    }

    public DataTypeHandler<?> getHandler(Class<?> clazz) {
        for (DataTypeHandler<?> typeHandler : this.typeHandlers) {
            if (typeHandler.matchesType(clazz)) {
                return typeHandler;
            }
        }
        return null;
    }

    public void registerDatabase(MysqlDatabase mysqlDatabase) {
        this.databases.put(mysqlDatabase.getDatabaseName(), mysqlDatabase);
    }
    
    public void addDatabaseHandler(DatabaseHandler databaseHandler) {
        this.databaseHandlers.add(databaseHandler);
        
        if (this.registeredDatabases) {
            databaseHandler.registerDatabases();
        }
        
        if (this.registeredTypeHandlers) {
            databaseHandler.registerTypeHandlers();
        }
        
        if (this.registeredRecordTypes) {
            databaseHandler.registerRecordTypes();
        }
    }
}
