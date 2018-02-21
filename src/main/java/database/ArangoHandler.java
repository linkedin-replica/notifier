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
        config = new ConfigReader("arango_names");
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
        String query = "For t in " + collectionName + " FILTER t.userId == @userId RETURN t";
        Map<String, Object> bindVars = new HashMap<>();
        bindVars.put("userId", userId);
        ArangoCursor<Notification> cursor = dbInstance.query(query, bindVars, null, Notification.class);

        ArrayList<Notification> result = new ArrayList<>();
        for(; cursor.hasNext();)
            result.add(cursor.next());
        return result;
    }

    public List<Notification> getUnreadNotifications(int userId) {
        // TODO
        return null;
    }

    @Override
    public void markAsRead(List<Notification> notifcations) {
        // TODO
    }
}
