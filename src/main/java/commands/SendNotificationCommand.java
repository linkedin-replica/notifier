package commands;


import models.Command;
import models.Notification;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SendNotificationCommand extends Command{
    public SendNotificationCommand(HashMap<String, String> args) {
        super(args);
    }

    public LinkedHashMap<String, Object> execute() {
        // validate that all required arguments are passed
        validateArgs(new String[]{"userId", "text", "link"});

        int userId = Integer.parseInt(args.get("userId"));
        String notificationText = args.get("text");
        String notificationLink = args.get("link");
        long timeStamp = System.currentTimeMillis();

        // insert new notification in db
        Notification newNotification =
                new Notification(notificationText,
                        notificationLink,
                        timeStamp,
                        false);
        dbHandler.sendNotification(userId, newNotification);

        return new LinkedHashMap<>();
    }
}
