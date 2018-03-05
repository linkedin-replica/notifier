package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private Properties commandNameToClass = new Properties();
    private Properties appConfig = new Properties();
    private Properties arangoConfig = new Properties();

    private volatile static ConfigReader instance;

    public static boolean isTesting;

    private ConfigReader() throws IOException {
        populateWithConfig("commands.config", commandNameToClass);
        populateWithConfig(isTesting ? "arango.test.config" : "arango.config", arangoConfig);
        populateWithConfig("app.config", appConfig);
    }

    private static void populateWithConfig(String configFileName, Properties properties) throws IOException {
        String configFolder = "src/main/resources/config/";
        FileInputStream inputStream = new FileInputStream(configFolder + configFileName);
        properties.load(inputStream);
        inputStream.close();
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
        String commandsPackageName = appConfig.getProperty("package.commands");
        String commandClass = commandsPackageName + '.' + commandNameToClass.get(commandName);
        return Class.forName(commandClass);
    }

    public Class getNoSqlHandler() throws ClassNotFoundException {
        String handlersPackageName = appConfig.getProperty("package.handlers");
        return Class.forName(handlersPackageName + '.' + appConfig.get("handler.nosql"));
    }

    public String getAppConfig(String key) {
        return appConfig.getProperty(key);
    }

    public String getArangoConfig(String key) {
        return arangoConfig.getProperty(key);
    }
}
