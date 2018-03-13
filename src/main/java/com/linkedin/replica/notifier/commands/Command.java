package com.linkedin.replica.notifier.commands;


import com.linkedin.replica.notifier.database.handlers.DatabaseHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class Command {
    protected HashMap<String, String> args;
    protected DatabaseHandler dbHandler;

    public Command(HashMap<String, String> args) {
        this.args = args;
    }

    /**
     * Execute the command
     * @return The output (if any) of the command
     */
    public abstract Object execute();

    /**
     * Set the configured db handler
     * @param dbHandler: The configured db handler
     */
    public void setDbHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    protected void validateArgs(String[] requiredArgs) {
        for(String arg: requiredArgs)
            if(!args.containsKey(arg)) {
                String exceptionMsg = String.format("Cannot execute command. %s argument is missing", arg);
                throw new IllegalArgumentException(exceptionMsg);
            }
    }
}
