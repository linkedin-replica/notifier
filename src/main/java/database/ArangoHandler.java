package database;


import models.Notification;

import java.util.List;

public class ArangoHandler implements DatabaseHandler{
    public void connect() {
        // TODO
    }

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

    public void disconnect() {
        // TODO
    }
}
