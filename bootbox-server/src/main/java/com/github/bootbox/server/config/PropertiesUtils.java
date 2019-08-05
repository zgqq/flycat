package com.github.bootbox.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    public static Properties loadProperties(String name) {
        final InputStream resourceAsStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(name);
        if (resourceAsStream == null) {
            return null;
        }
        final Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties file", e);
        }
    }
}
