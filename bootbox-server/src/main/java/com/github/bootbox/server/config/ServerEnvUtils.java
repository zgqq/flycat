package com.github.bootbox.server.config;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ServerEnvUtils {

    private static volatile WeakReference<List<Properties>> propertiesRef;

    public static String getCurrentProfile() {
        return System.getProperty("spring.profiles.active");
    }

    public static List<String> getCurrentPossibleConfigProperties() {
        final String currentProfile = getCurrentProfile();
        return Arrays.asList("application-" + currentProfile + ".properties",
                "config/application-" + currentProfile + ".properties",
                "application.properties");
    }

    public static Integer getIntegerProperty(String name) {
        final String property = getProperty(name);
        if (property == null) {
            return null;
        }
        return Integer.valueOf(property);
    }

    public static String getProperty(String name) {
        final List<Properties> configProperties = getConfigProperties();
        return getProperty(configProperties, name, null);
    }

    public static String getProperty(List<Properties> properties, String prop,
                                     String defaultValue) {
        for (Properties property : properties) {
            final String name = property.getProperty(prop);
            if (StringUtils.isNotBlank(name)) {
                return name;
            }
        }
        return defaultValue;
    }


    public static String getProperty(List<Properties> properties, List<String> props,
                                     String defaultValue) {
        for (Properties property : properties) {
            for (String prop : props) {
                final String name = property.getProperty(prop);
                if (StringUtils.isNotBlank(name)) {
                    return name;
                }
            }
        }
        return defaultValue;
    }

    public static List<Properties> getConfigProperties() {
        boolean reload = true;
        List<Properties> propertiesList = null;
        if (propertiesRef != null) {
            propertiesList = propertiesRef.get();
            reload = propertiesList == null;
        }

        if (reload) {
            final List<String> currentPossibleConfigProperties = getCurrentPossibleConfigProperties();
            propertiesList = currentPossibleConfigProperties.stream().map(PropertiesUtils::loadProperties)
                    .filter(properties -> properties != null)
                    .collect(Collectors.toList());
            propertiesRef = new WeakReference<>(propertiesList);
        }
        return propertiesList;
    }
}
