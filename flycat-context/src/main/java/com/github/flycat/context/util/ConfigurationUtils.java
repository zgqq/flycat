package com.github.flycat.context.util;

import com.github.flycat.context.ApplicationConfiguration;
import com.google.common.base.Strings;

public class ConfigurationUtils {


    public static Integer getInteger(ApplicationConfiguration applicationConfiguration, String key) {
        if (applicationConfiguration != null) {
            Integer value = applicationConfiguration.getInteger(key);
            if (value != null) {
                return Integer.valueOf(value);
            }
        }

        String property = ServerEnvUtils.getProperty(key);
        if (Strings.isNullOrEmpty(property)) {
            return null;
        }
        return Integer.valueOf(property);
    }

    public static String getString(ApplicationConfiguration applicationConfiguration, String key) {
        if (applicationConfiguration != null) {
            String value = applicationConfiguration.getString(key);
            if (value != null) {
                return value;
            }
        }
        return ServerEnvUtils.getProperty(key);
    }
}
