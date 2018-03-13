package com.linkedin.replica.notifier.main;

import com.linkedin.replica.notifier.config.Configuration;
import com.linkedin.replica.notifier.database.DatabaseConnection;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public void start(String... args) throws ClassNotFoundException, IOException, SQLException {
        if(args.length != 3)
            throw new IllegalArgumentException("Expected three arguments. 1- database config file path "
                    + "2- command config file path  3- arango name file path");

        // create singleton instance of Configuration class that will hold configuration files paths
        Configuration.init(args[0], args[1], args[2]);

        // create singleton instance of DatabaseConnection class that is responsible for intiating connections
        // with databases
        DatabaseConnection.init();
    }

    public static void shutdown() throws ClassNotFoundException, IOException, SQLException{
        DatabaseConnection.getInstance().closeConnections();
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
        new Main().start(args);
    }
}
