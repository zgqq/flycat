/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.context.util;

import com.github.flycat.util.ArrayUtils;
import com.github.flycat.util.StringUtils;
import com.github.flycat.util.properties.PropertiesUtils;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ServerEnvUtils {

    private static volatile WeakReference<Map<String, Properties>> propertiesMapRef;
    private static volatile WeakReference<List<Properties>> propertiesRef;

    private static volatile WeakReference<List<Properties>> propertiesMainRef;

    public static String getCurrentProfile() {
        String property = System.getProperty("spring.profiles.active");
        if (StringUtils.isNotBlank(property)) {
            return property;
        }
        List<Properties> configMainProperties = getConfigMainProperties();
        return getProperty(configMainProperties, "spring.profiles.active", null);
    }


    public static List<String> getCurrentPossibleMainConfigProperties() {
        return Arrays.asList(
                "config/application.properties",
                "application.properties");
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
//            propertiesList = currentPossibleConfigProperties.stream().map(PropertiesUtils::loadProperties)
//                    .filter(properties -> properties != null)
//                    .collect(Collectors.toList());
//            propertiesRef = new WeakReference<>(propertiesList);
            List<Properties> configProperties = getConfigProperties(currentPossibleConfigProperties);
            propertiesRef = new WeakReference<>(configProperties);
            return configProperties;
        }
        return propertiesList;
    }


    public static List<Properties> getConfigMainProperties() {
        boolean reload = true;
        List<Properties> propertiesList = null;
        if (propertiesMainRef != null) {
            propertiesList = propertiesMainRef.get();
            reload = propertiesList == null;
        }
        if (reload) {
            final List<String> currentPossibleConfigProperties = getCurrentPossibleMainConfigProperties();
            List<Properties> configProperties = getConfigProperties(currentPossibleConfigProperties);
            propertiesMainRef = new WeakReference<>(configProperties);
            return configProperties;
        }
        return propertiesList;
    }


    public static List<Properties> getConfigProperties(List<String> profiles) {
        boolean reload = true;
        Map<String, Properties> propertiesMap = null;
        ArrayList<Properties> objects = new ArrayList<>();
        if (propertiesMapRef != null && propertiesMapRef.get() != null) {
            propertiesMap = propertiesMapRef.get();
            for (String profile : profiles) {
                Properties properties = propertiesMap.computeIfAbsent(profile,
                        PropertiesUtils::loadProperties);
                if (properties != null) {
                    objects.add(properties);
                }
            }
        } else {
            propertiesMap = new ConcurrentHashMap<>();
            for (String profile : profiles) {
                Properties properties = propertiesMap.computeIfAbsent(profile,
                        PropertiesUtils::loadProperties);
                if (properties != null) {
                    objects.add(properties);
                }
            }
            propertiesMapRef = new WeakReference<>(propertiesMap);
        }
        return objects;
    }

}
