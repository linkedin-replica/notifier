package models;

/**
 * Holder model for the notification
 */
public class Notification {
    private int id;
    private String text, link, timeStamp;
    private boolean read;
    Notification(int id, String text, String link, String timeStamp, boolean read) {
        this.id = id;
        this.text = text;
        this.link = link;
        this.timeStamp = timeStamp;
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public boolean isRead() {
        return read;
    }
}
