package database;

import models.Notification;

import java.util.List;

public interface DatabaseHandler {
    /**
     * Initiate a connection with the database
     */
    void connect();

    /**
     * Send a new notification to the user
     * @param userId: The user to send the notification to
     * @param notification: The new notification
     */
    void sendNotification(int userId, Notification notification);

    /**
     * Get all notifications for a user
     * @param userId: The user owning the notifications
     * @return The notifications
     */
    List<Notification> getAllNotifications(int userId);

    /**
     * Get all unread notifications for a user
     * @param userId: The user owning the notifications
     * @return The new notifications
     */
    List<Notification> getUnreadNotifications(int userId);

    /**
     * Close a connection with the database
     */
    void disconnect();
}
