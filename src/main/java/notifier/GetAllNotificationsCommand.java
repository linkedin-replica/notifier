package notifier;


import models.Command;

import java.util.HashMap;

public class GetAllNotificationsCommand extends Command{
    GetAllNotificationsCommand(HashMap<String, String> args) {
        super(args);
    }

    public String execute() {
        // TODO
        return null;
    }
}
