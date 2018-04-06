package com.linkedin.replica.notifier.commands.impl;


import com.linkedin.replica.notifier.cache.NotifierCacheHandler;
import com.linkedin.replica.notifier.commands.Command;
import com.linkedin.replica.notifier.database.handlers.NotificationsHandler;
import com.linkedin.replica.notifier.models.Notification;
import com.sun.tools.corba.se.idl.constExpr.Not;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SendNotificationCommand extends Command {

    private NotifierCacheHandler notifierCacheHandler;

    public SendNotificationCommand(HashMap<String, Object> args) {
        super(args);
    }

    public Object execute() {
        NotificationsHandler dbHandler = (NotificationsHandler) this.dbHandler;
        // validate that all required arguments are passed
        validateArgs(new String[]{"userId", "text", "link"});
        notifierCacheHandler = (NotifierCacheHandler) cacheHandler;
        String userId = args.get("userId").toString();
        String notificationText = args.get("text").toString();
        String notificationLink = args.get("link").toString();
        long timeStamp = System.currentTimeMillis();

        // insert new notification in db
        Notification newNotification =
                new Notification(notificationText,
                        notificationLink,
                        timeStamp,
                        false);
        dbHandler.sendNotification(userId, newNotification);
        try {
            notifierCacheHandler.saveNotification(userId, newNotification);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
