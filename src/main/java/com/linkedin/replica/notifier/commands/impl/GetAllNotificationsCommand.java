package com.linkedin.replica.notifier.commands.impl;


import com.linkedin.replica.notifier.commands.Command;
import com.linkedin.replica.notifier.database.handlers.NotificationsHandler;
import com.linkedin.replica.notifier.models.Notification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GetAllNotificationsCommand extends Command {
    public GetAllNotificationsCommand(HashMap<String, Object> args) {
        super(args);
    }

    public Object execute() {
        NotificationsHandler dbHandler = (NotificationsHandler) this.dbHandler;
        // validate that all required arguments are passed
        validateArgs(new String[]{"userId"});
        System.out.println("User " + args.get("userId") + " gets all nots.");
        // get notifications from db
        return dbHandler.getAllNotifications(args.get("userId").toString());
    }
}
