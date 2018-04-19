package com.linkedin.replica.notifier.models;

import com.arangodb.entity.DocumentField;

/**
 * Holder model for the notification
 */
public class Notification {
    @DocumentField(DocumentField.Type.KEY)
    private String notificationId;
    private String text, link;
    private long timestamp;
    private String userId;
    private boolean read;

    public Notification(String id, String text, String link, String userId, long timestamp, boolean read) {
        this.userId = userId;
        this.text = text;
        this.link = link;
        this.timestamp = timestamp;
        this.read = read;
        this.notificationId = id;
    }

    public Notification() {}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public String getNotificationId() {
        return notificationId;
    }
}
