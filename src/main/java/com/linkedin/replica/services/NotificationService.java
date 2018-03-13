package com.linkedin.replica.services;

import com.linkedin.replica.database.handlers.DatabaseHandler;
import com.linkedin.replica.commands.Command;
import com.linkedin.replica.config.Configuration;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class NotificationService {
    private Configuration config;

    public NotificationService() throws IOException {
        config = Configuration.getInstance();
    }

    public LinkedHashMap<String, Object> serve(String commandName, HashMap<String, String> args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> commandClass = config.getCommandClass(commandName);
        Constructor constructor = commandClass.getConstructor(HashMap.class);
        Command command = (Command) constructor.newInstance(args);

        Class<?> dbHandlerClass = config.getHandlerClass(commandName);
        DatabaseHandler dbHandler = (DatabaseHandler) dbHandlerClass.newInstance();

        command.setDbHandler(dbHandler);

        return command.execute();
    }
}
