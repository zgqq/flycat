package com.github.flycat.context.util;

import com.github.flycat.context.ApplicationConfiguration;
import com.google.common.base.Strings;

public class ConfigurationUtils {


    public static Integer getInteger(ApplicationConfiguration applicationConfiguration, String key) {
        if (applicationConfiguration != null) {
            return applicationConfiguration.getInteger(key);
        }

        String property = ServerEnvUtils.getProperty(key);
        if (Strings.isNullOrEmpty(property)) {
            return null;
        }
        return Integer.valueOf(property);
    }

    public static String getString(ApplicationConfiguration applicationConfiguration, String key) {
        if (applicationConfiguration != null) {
            return applicationConfiguration.getString(key);
        }
        return ServerEnvUtils.getProperty(key);
    }
}
