package com.linkedin.replica.notifier.commands.impl;


import com.linkedin.replica.notifier.commands.Command;
import com.linkedin.replica.notifier.database.handlers.NotificationsHandler;
import com.linkedin.replica.notifier.models.Notification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public class SendNotificationCommand extends Command {
    public SendNotificationCommand(HashMap<String, Object> args) {
        super(args);
    }

    public Object execute() {
        NotificationsHandler dbHandler = (NotificationsHandler) this.dbHandler;
        // validate that all required arguments are passed
        validateArgs(new String[]{"userId", "text", "link"});
        String notificationId = UUID.randomUUID().toString();
        String userId = args.get("userId").toString();
        String notificationText = args.get("text").toString();
        String notificationLink = args.get("link").toString();
        long timeStamp = System.currentTimeMillis();

        // insert new notification in db
        Notification newNotification =
                new Notification(notificationId,
                        notificationText,
                        notificationLink,
                        userId,
                        timeStamp,
                        false);
        dbHandler.sendNotification(newNotification);

        return null;
    }
}
