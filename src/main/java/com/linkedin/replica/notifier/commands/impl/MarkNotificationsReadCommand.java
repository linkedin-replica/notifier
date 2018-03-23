package com.linkedin.replica.notifier.commands.impl;


import com.linkedin.replica.notifier.commands.Command;
import com.linkedin.replica.notifier.database.handlers.NotificationsHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MarkNotificationsReadCommand extends Command {
    public MarkNotificationsReadCommand(HashMap<String, Object> args) {
        super(args);
    }

    public Object execute() {
        NotificationsHandler dbHandler = (NotificationsHandler) this.dbHandler;
        // validate that all required arguments are passed
        validateArgs(new String[]{"userId"});

        String userId = args.get("userId").toString();

        // mark notifications as read in db
        dbHandler.markAllNotificationsAsRead(userId);

        return null;
    }
}
