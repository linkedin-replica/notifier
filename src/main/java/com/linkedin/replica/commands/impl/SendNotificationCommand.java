package com.linkedin.replica.commands.impl;


import com.linkedin.replica.commands.Command;
import com.linkedin.replica.database.handlers.NotificationsHandler;
import com.linkedin.replica.models.Notification;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SendNotificationCommand extends Command {
    public SendNotificationCommand(HashMap<String, String> args) {
        super(args);
    }

    public LinkedHashMap<String, Object> execute() {
        NotificationsHandler dbHandler = (NotificationsHandler) this.dbHandler;
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