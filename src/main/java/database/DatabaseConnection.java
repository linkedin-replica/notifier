package database;

import com.arangodb.ArangoDB;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A singleton class carrying a database instance
 */
public class DatabaseConnection {
    private ArangoDB arangoDBInstance;
    private DatabaseHandler dbHandler;
    private Properties config;

    private static DatabaseConnection dbConnection;

    private DatabaseConnection() throws IOException {
        config = new Properties();
        config.load(new FileInputStream("config"));

        initializeArangoDB();
    }

    private void initializeArangoDB() {
        arangoDBInstance = new ArangoDB.Builder()
                .user(config.getProperty("arangodb.user"))
                .password(config.getProperty("arangodb.password"))
                .build();
    }

    /**
     * Get a singleton DB instance
     * @return The DB instance
     */
    public static DatabaseConnection getDBConnection() throws IOException {
        if(dbConnection == null)
            dbConnection = new DatabaseConnection();
        return dbConnection;
    }


    public ArangoDB getArangoInstance() {
        return arangoDBInstance;
    }
}
