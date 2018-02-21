package database;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import models.Notification;
import utils.ConfigReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArangoHandler implements DatabaseHandler{
    private ConfigReader config;
    private ArangoDatabase dbInstance;
    private ArangoCollection collection;
    private String collectionName;

    public ArangoHandler() throws IOException {
        // read arango constants
        config = new ConfigReader("arango_names");

        // init db
        ArangoDB arangoDriver = DatabaseConnection.getDBConnection().getArangoDriver();
        collectionName = config.getConfig("collection.notifications.name");
        dbInstance = arangoDriver.db(config.getConfig("db.name"));
        collection = dbInstance.collection(collectionName);
    }

    public void sendNotification(int userId, Notification notification) throws IOException {
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
    public void markAsRead(List<Notification> notifcations) {
        // TODO
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
