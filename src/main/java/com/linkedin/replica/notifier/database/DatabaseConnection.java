package com.linkedin.replica.notifier.database;

import com.arangodb.ArangoDB;
import com.linkedin.replica.notifier.config.Configuration;

import java.io.IOException;

/**
 * A singleton class carrying a com.linkedin.replica.database instance
 */
public class DatabaseConnection {
    private ArangoDB arangoDriver;
    private Configuration config;

    private static DatabaseConnection dbConnection;

    private DatabaseConnection() throws IOException {
        config = Configuration.getInstance();

        initializeArangoDB();
    }

    private void initializeArangoDB() {
        arangoDriver = new ArangoDB.Builder()
                .host(config.getArangoConfigProp("arangodb.host"),
                        Integer.parseInt(config.getArangoConfigProp("arangodb.port")))
                .user(config.getArangoConfigProp("arangodb.user"))
                .password(config.getArangoConfigProp("arangodb.password"))
                .build();
    }

    public static void init() throws IOException {
        dbConnection = new DatabaseConnection();
    }

    /**
     * Get a singleton DB instance
     * @return The DB instance
     */
    public static DatabaseConnection getInstance() throws IOException {
        return dbConnection;
    }


    public ArangoDB getArangoDriver() {
        return arangoDriver;
    }

    public void closeConnections() {
        arangoDriver.shutdown();
    }
}
