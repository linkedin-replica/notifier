package com.linkedin.replica.commands.impl;


import com.linkedin.replica.commands.Command;
import com.linkedin.replica.database.handlers.NotificationsHandler;
import com.linkedin.replica.models.Notification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GetAllNotificationsCommand extends Command {
    public GetAllNotificationsCommand(HashMap<String, String> args) {
        super(args);
    }

    public LinkedHashMap<String, Object> execute() {
        NotificationsHandler dbHandler = (NotificationsHandler) this.dbHandler;
        // validate that all required arguments are passed
        validateArgs(new String[]{"userId"});

        // get notifications from db
        List<Notification> notifications = dbHandler.getAllNotifications(Integer.parseInt(args.get("userId")));

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("results", notifications);
        return result;
    }
}
