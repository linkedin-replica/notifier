package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private Properties properties;

    public ConfigReader(String configFileName) throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/config/" + configFileName));
    }

    public String getConfig(String key) {
        return properties.getProperty(key);
    }
}
