package com.linkedin.replica.models;

/**
 * Holder model for the notification
 */
public class Notification {
    private String notificationText, link;
    private long timeStamp;
    private int userId;
    private boolean read;
    public Notification(String text, String link, long timeStamp, boolean read) {
        this.notificationText = text;
        this.link = link;
        this.timeStamp = timeStamp;
        this.read = read;
    }

    public Notification() {}

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public String getLink() {
        return link;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public boolean isRead() {
        return read;
    }
}