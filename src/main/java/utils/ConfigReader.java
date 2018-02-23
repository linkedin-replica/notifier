package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class ConfigReader {

    private HashMap<String, String> commandNameToClass = new HashMap<>();
    private HashMap<String, String> appConfig = new HashMap<>();
    private HashMap<String, String> arangoConfig = new HashMap<>();

    private volatile static ConfigReader instance;

    private ConfigReader() throws IOException {
        populateMapWithConfig("commands.config", commandNameToClass);
        populateMapWithConfig("arango.config", arangoConfig);
        populateMapWithConfig("app.config", appConfig);
    }

    private static void populateMapWithConfig(String configFileName, HashMap<String, String> map) throws IOException {
        String configFolder = "src/main/resources/config/";
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(configFolder + configFileName);
        properties.load(inputStream);
        inputStream.close();

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            map.put(key, value);
        }
    }

    public static ConfigReader getInstance() throws IOException {
        if(instance == null) {
            synchronized (ConfigReader.class) {
                if(instance == null)
                    instance = new ConfigReader();
            }
        }

        return instance;
    }

    public Class getCommandClass(String commandName) throws ClassNotFoundException {
        String commandsPackageName = appConfig.get("package.commands");
        String commandClass = commandsPackageName + '.' + commandNameToClass.get(commandName);
        return Class.forName(commandClass);
    }

    public Class getNoSqlHandler() throws ClassNotFoundException {
        String handlersPackageName = appConfig.get("package.handlers");
        return Class.forName(handlersPackageName + '.' + appConfig.get("handler.nosql"));
    }

    public String getArangoConfig(String key) {
        return arangoConfig.get(key);
    }
}
