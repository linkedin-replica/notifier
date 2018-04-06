package com.linkedin.replica.notifier.services;

import com.linkedin.replica.notifier.cache.CacheHandler;
import com.linkedin.replica.notifier.database.handlers.DatabaseHandler;
import com.linkedin.replica.notifier.commands.Command;
import com.linkedin.replica.notifier.config.Configuration;

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

    public Object serve(String commandName, HashMap<String, String> args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> commandClass = config.getCommandClass(commandName);
        Constructor constructor = commandClass.getConstructor(HashMap.class);
        Command command = (Command) constructor.newInstance(args);

        Class<?> dbHandlerClass = config.getHandlerClass(commandName);
        Class<?> cacheHandlerClass = config.getCacheHandlerClass(commandName);

        DatabaseHandler dbHandler = (DatabaseHandler) dbHandlerClass.newInstance();
        CacheHandler cacheHandler = (CacheHandler) cacheHandlerClass.newInstance();
        command.setDbHandler(dbHandler);
        command.setCacheHandler(cacheHandler);

        return command.execute();
    }
}
