package com.linkedin.replica.notifier.database.handlers.impl;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.linkedin.replica.notifier.database.DatabaseConnection;
import com.linkedin.replica.notifier.database.handlers.NotificationsHandler;
import com.linkedin.replica.notifier.models.Notification;
import com.linkedin.replica.notifier.config.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArangoNotificationsHandler implements NotificationsHandler {
    private ArangoDatabase dbInstance;
    private ArangoCollection collection;
    private String collectionName;

    public ArangoNotificationsHandler() throws IOException {
        // init db
        Configuration config = Configuration.getInstance();
        ArangoDB arangoDriver = DatabaseConnection.getInstance().getArangoDriver();
        collectionName = config.getArangoConfig("collection.notifications.name");
        dbInstance = arangoDriver.db(config.getArangoConfig("db.name"));
        collection = dbInstance.collection(collectionName);
    }

    public void sendNotification(int userId, Notification notification) {
        notification.setUserId(userId);
        collection.insertDocument(notification);
    }

    public List<Notification> getAllNotifications(int userId) {
        // form db query
        String query = "For t in " + collectionName + " FILTER t.userId == @userId RETURN t";
        return getNotificationsFromDB(query, userId);
    }

    public List<Notification> getUnreadNotifications(int userId) {
        // form query db
        String query = "For t in " + collectionName + " FILTER " +
                "t.userId == @userId &&" +
                " t.read == false" +
                " RETURN t";

        return getNotificationsFromDB(query, userId);
    }

    @Override
    public void markAllNotificationsAsRead(int userId) {
        // form the query
        String query = "FOR t in " + collectionName + " FILTER" +
                " t.userId == @userId &&" +
                " t.read == false" +
                " UPDATE { _key: t._key, read: true } IN " + collectionName;

        // bind the params
        Map<String, Object> bindVars = new HashMap<>();
        bindVars.put("userId", userId);

        // execute the query
        dbInstance.query(query, bindVars, null, null);
    }

    /**
     * Return notifications from db based on a certain query
     * @param query: The query to fetch the notifications
     * @param userId: The owner of the notifications
     * @return The queried notifications
     */
    private List<Notification> getNotificationsFromDB(String query, int userId) {
        // bind the variables in the query
        Map<String, Object> bindVars = new HashMap<>();
        bindVars.put("userId", userId);

        // process query
        ArangoCursor<Notification> cursor = dbInstance.query(query, bindVars, null, Notification.class);

        ArrayList<Notification> result = new ArrayList<>();
        for(; cursor.hasNext();)
            result.add(cursor.next());
        return result;
    }
}
