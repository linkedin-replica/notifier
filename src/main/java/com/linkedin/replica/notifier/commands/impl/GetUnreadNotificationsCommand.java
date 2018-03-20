package com.linkedin.replica.notifier.commands.impl;


import com.linkedin.replica.notifier.commands.Command;
import com.linkedin.replica.notifier.database.handlers.NotificationsHandler;
import com.linkedin.replica.notifier.models.Notification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GetUnreadNotificationsCommand extends Command {
    public GetUnreadNotificationsCommand(HashMap<String, Object> args) {
        super(args);
    }

    public Object execute() {
        NotificationsHandler dbHandler = (NotificationsHandler) this.dbHandler;
        // validate that all required arguments are passed
        validateArgs(new String[]{"userId"});

        // get unread notifications from db
        return dbHandler.getUnreadNotifications(Integer.parseInt(args.get("userId").toString()));
    }
}
