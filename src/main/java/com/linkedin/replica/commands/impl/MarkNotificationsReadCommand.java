package com.linkedin.replica.commands.impl;


import com.linkedin.replica.commands.Command;
import com.linkedin.replica.database.handlers.NotificationsHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MarkNotificationsReadCommand extends Command {
    public MarkNotificationsReadCommand(HashMap<String, String> args) {
        super(args);
    }

    public LinkedHashMap<String, Object> execute() {
        NotificationsHandler dbHandler = (NotificationsHandler) this.dbHandler;
        // validate that all required arguments are passed
        validateArgs(new String[]{"userId"});

        int userId = Integer.parseInt(args.get("userId"));

        // mark notifications as read in db
        dbHandler.markAllNotificationsAsRead(userId);

        return new LinkedHashMap<>();
    }
}
