package notifier;


import models.Command;

import java.util.HashMap;

public class GetUnreadNotificationsCommand extends Command{
    GetUnreadNotificationsCommand(HashMap<String, String> args) {
        super(args);
    }

    public String execute() {
        // TODO
        return null;
    }
}
