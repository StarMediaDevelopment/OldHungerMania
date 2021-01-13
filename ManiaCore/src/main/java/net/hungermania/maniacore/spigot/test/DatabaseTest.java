package net.hungermania.maniacore.spigot.test;

import net.hungermania.maniacore.api.ManiaCore;
import net.hungermania.manialib.data.DatabaseManager;
import net.hungermania.manialib.data.MysqlDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class DatabaseTest {
    
    public static void test(ManiaCore maniaCore) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("./mania-mysql.properties"));
        properties.setProperty("mysql-database", "test");
        DatabaseManager databaseManager = new DatabaseManager();
        MysqlDatabase mysqlDatabase = new MysqlDatabase(properties, maniaCore.getLogger(), databaseManager);
        databaseManager.registerRecord(TestUser.class, mysqlDatabase);
        mysqlDatabase.generateTables();
        List<TestUser> testUsers = new ArrayList<>();
        Random random = new Random();
        boolean last = false;
        for (int i = 0; i < 10; i++) {
            TestUser user = new TestUser(UUID.randomUUID(), "User" + i, last = !last, System.currentTimeMillis() - random.nextInt(100000000), random.nextDouble());
            testUsers.add(user);
        }
        
        for (TestUser testUser : testUsers) {
            mysqlDatabase.pushRecord(testUser);
        }

        TestUser id3 = mysqlDatabase.getRecord(TestUser.class, "id", 3);
        id3.setName("ModifiedUser" + id3.getId());
        mysqlDatabase.pushRecord(id3);
        System.out.println(id3);
    }
}
