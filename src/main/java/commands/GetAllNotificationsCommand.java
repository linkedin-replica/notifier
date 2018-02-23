package commands;


import models.Command;
import models.Notification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GetAllNotificationsCommand extends Command{
    GetAllNotificationsCommand(HashMap<String, String> args) {
        super(args);
    }

    public LinkedHashMap<String, Object> execute() {
        // validate that all required arguments are passed
        validateArgs(new String[]{"userId"});

        // get notifications from db
        List<Notification> notifications = dbHandler.getAllNotifications(Integer.parseInt(args.get("userId")));

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("results", notifications);
        return result;
    }
}
