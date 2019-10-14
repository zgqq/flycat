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
package com.github.flycat.context;

import com.github.flycat.util.StringUtils;
import com.google.common.base.CaseFormat;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ApplicationConfiguration {

    default <T> T load(String prefix, Class<T> clazz) throws ConfigurationException {
        try {
            T instance = clazz.getConstructor().newInstance();
            return (T) load(prefix, clazz, instance);
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

    default Object load(String prefix, Class clazz, Object instance) throws Exception {
        final Field[] declaredFields = clazz.getDeclaredFields();
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
                } else if (String[].class.isAssignableFrom(type)) {
                    final String[] stringArray = getStringArray(key);
                    method.invoke(instance, new Object[]{stringArray});
                } else {
                    declaredField.setAccessible(true);
                    final Object o = declaredField.get(instance);
                    if (o != null) {
                        load(key, o.getClass(), o);
                    } else {
                        throw new ConfigurationException("Unsupported type " + type.getName() + ", name " + name + ""
                                + " from class " + clazz.getName());
                    }
                }
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
        return instance;
    }

    String getStringValue(String key);

    default <T> T getObject(String key, Class<T> clazz) {
        final String value = getStringValue(key);
        PropertyEditor editor = PropertyEditorManager.findEditor(clazz);
        editor.setAsText(value);
        return (T) editor.getValue();
    }

    default <T> T retryGet(String key, Class<T> clazz) {
        final String toKey = toHyphenKey(key);
        final T value = getObject(toKey, clazz);
        if (value != null) {
            return value;
        }
        return this.getObject(key, clazz);
    }


    default String getString(String key) {
        return retryGet(key, String.class);
    }

    default String[] getStringArray(String key) {
        final String array = getString(key);
        if (StringUtils.isBlank(array)) {
            return new String[]{};
        }
        return array.split(",");
    }

    default String toHyphenKey(String key) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, key);
    }

    default Integer getInteger(String key) {
        return retryGet(key, Integer.class);
    }

    default Boolean getBoolean(String key) {
        return retryGet(key, Boolean.class);
    }
}
