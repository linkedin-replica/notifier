package database;

import models.Notification;

import java.io.IOException;
import java.util.List;

public interface DatabaseHandler {
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
     * Mark all notifications of a user as read
     * @param userId: The owner of the notifications
     */
    void markAllNotificationsAsRead(int userId);
}
