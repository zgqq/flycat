/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.spi.context;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ApplicationConfiguration {
    String getString(String key);

    Integer getInteger(String key);

    Boolean getBoolean(String key);

    default <T> T load(String prefix, Class<T> clazz) throws ConfigurationException {
        try {
            final Field[] declaredFields = clazz.getDeclaredFields();
            T instance = clazz.getConstructor().newInstance();
            for (int i = 0; i < declaredFields.length; i++) {
                final Field declaredField = declaredFields[i];
                final String name = declaredField.getName();
                final Class<?> type = declaredField.getType();
                try {
                    final Method method = clazz.getMethod("set" + StringUtils.capitalize(name), declaredField.getType());
                    final String key = prefix + "." + name;
                    if (Integer.class.isAssignableFrom(type)) {
                        method.invoke(instance, getInteger(key));
                    } else if (String.class.isAssignableFrom(type)) {
                        method.invoke(instance, getString(key));
                    } else if (boolean.class.isAssignableFrom(type)) {
                        final Boolean aBoolean = getBoolean(key);
                        if (aBoolean != null) {
                            method.invoke(instance, aBoolean);
                        }
                    } else {
                        throw new ConfigurationException("Unsupported type " + type.getName()
                                + " from class " + clazz.getName());
                    }
                } catch (NoSuchMethodException e) {
                    continue;
                }
            }
            return instance;
        } catch (Throwable e) {
            if (e instanceof ConfigurationException) {
                ConfigurationException configurationException = (ConfigurationException) e;
                throw configurationException;
            } else {
                throw new ConfigurationException("Unable to load config, prefix:" + prefix + ", class:" + clazz.getName(),
                        e);
            }
        }
    }
}
