package com.linkedin.replica.database;

import com.arangodb.ArangoDB;
import com.linkedin.replica.config.Configuration;

import java.io.IOException;

/**
 * A singleton class carrying a com.linkedin.replica.database instance
 */
public class DatabaseConnection {
    private ArangoDB arangoDriver;
    private Configuration config;

    private volatile static DatabaseConnection dbConnection;

    private DatabaseConnection() throws IOException {
        config = Configuration.getInstance();

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
    public static DatabaseConnection getDBConnection() throws IOException {
        if(dbConnection == null) {
            synchronized (DatabaseConnection.class) {
                if (dbConnection == null)
                    dbConnection = new DatabaseConnection();
            }
        }
        return dbConnection;
    }


    public ArangoDB getArangoDriver() {
        return arangoDriver;
    }

    public void closeConnections() {
        arangoDriver.shutdown();
    }
}
