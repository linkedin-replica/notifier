package notifier;


import database.ArangoHandler;
import database.DatabaseHandler;
import models.Command;

import java.util.HashMap;

public class SendNotificationCommand extends Command{
    SendNotificationCommand(HashMap<String, String> args) {
        super(args);
    }

    public String execute() {
        // TODO
        return null;
    }
}
