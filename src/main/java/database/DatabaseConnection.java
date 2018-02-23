package database;

import com.arangodb.ArangoDB;
import utils.ConfigReader;

import java.io.IOException;

/**
 * A singleton class carrying a database instance
 */
class DatabaseConnection {
    private ArangoDB arangoDriver;
    private ConfigReader config;

    private volatile static DatabaseConnection dbConnection;

    private DatabaseConnection() throws IOException {
        config = ConfigReader.getInstance();

        initializeArangoDB();
    }

    private void initializeArangoDB() {
        arangoDriver = new ArangoDB.Builder()
                .user(config.getArangoConfig("arangodb.user"))
                .password(config.getArangoConfig("arangodb.password"))
                .build();
    }

    /**
     * Get a singleton DB instance
     * @return The DB instance
     */
    static DatabaseConnection getDBConnection() throws IOException {
        if(dbConnection == null) {
            synchronized (DatabaseConnection.class) {
                if (dbConnection == null)
                    dbConnection = new DatabaseConnection();
            }
        }
        return dbConnection;
    }


    ArangoDB getArangoDriver() {
        return arangoDriver;
    }
}
