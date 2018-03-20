package com.linkedin.replica.notifier.main;

import com.linkedin.replica.notifier.config.Configuration;
import com.linkedin.replica.notifier.database.DatabaseConnection;
import com.linkedin.replica.notifier.messaging.ClientMessagesReceiver;
import com.linkedin.replica.notifier.messaging.ServicesMessagesReceiver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class Main {
    private ClientMessagesReceiver clientMessagesReceiver;
    private ServicesMessagesReceiver servicesMessagesReceiver;

    public void start(String... args) throws ClassNotFoundException, IOException, SQLException, TimeoutException {
        if(args.length != 3)
            throw new IllegalArgumentException("Expected three arguments. 1- application config file path "
                    + "2- arango config file path  3- commands config file path");

        // create singleton instance of Configuration class that will hold configuration files paths
        Configuration.init(args[0], args[1], args[2]);

        // create singleton instance of DatabaseConnection class that is responsible for intiating connections
        // with databases
        DatabaseConnection.init();

        // Create the message receivers
        clientMessagesReceiver = new ClientMessagesReceiver();
        servicesMessagesReceiver = new ServicesMessagesReceiver();
    }

    public void shutdown() throws ClassNotFoundException, IOException, SQLException, TimeoutException {
        DatabaseConnection.getInstance().closeConnections();
        clientMessagesReceiver.closeConnection();
        servicesMessagesReceiver.closeConnection();
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException, TimeoutException {
        new Main().start(args);
    }
}