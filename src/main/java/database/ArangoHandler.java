package database;


import com.arangodb.ArangoDB;
import models.Notification;

import java.io.IOException;
import java.util.List;

public class ArangoHandler implements DatabaseHandler{
    public void sendNotification(int userId, Notification notification) {
        // TODO
    }

    public List<Notification> getAllNotifications(int userId) {
        // TODO
        return null;
    }

    public List<Notification> getUnreadNotifications(int userId) {
        // TODO
        return null;
    }
}
