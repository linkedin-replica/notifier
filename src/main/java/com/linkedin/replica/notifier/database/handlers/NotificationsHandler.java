package com.linkedin.replica.notifier.database.handlers;

import com.linkedin.replica.notifier.models.Notification;

import java.util.List;

public interface NotificationsHandler extends DatabaseHandler{
    /**
     * Send a new notification to the user
     * @param notification: The new notification
     */
    void sendNotification(Notification notification);

    /**
     * Get all notifications for a user
     * @param userId: The user owning the notifications
     * @return The notifications
     */
    List<Notification> getAllNotifications(String userId);

    /**
     * Get all unread notifications for a user
     * @param userId: The user owning the notifications
     * @return The new notifications
     */
    List<Notification> getUnreadNotifications(String userId);


    /**
     * Mark all notifications of a user as read
     * @param userId: The owner of the notifications
     */
    void markAllNotificationsAsRead(String userId);
}
