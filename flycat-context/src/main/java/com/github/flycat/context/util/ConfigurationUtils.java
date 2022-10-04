package com.github.flycat.context.util;

import com.github.flycat.context.ApplicationConfiguration;

public class ConfigurationUtils {

    public static String getString(ApplicationConfiguration applicationConfiguration, String key) {
        if (applicationConfiguration != null) {
            return applicationConfiguration.getString(key);
        }
        return ServerEnvUtils.getProperty(key);
    }
}
