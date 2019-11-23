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

import com.github.flycat.util.properties.ServerEnvUtils;
import com.google.common.base.CaseFormat;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class ContextFreeConfiguration {
    private final ApplicationConfiguration applicationConfiguration;

    public ContextFreeConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public String getString(String key) {
        return retryGet(key, String.class);
    }

    public Integer getInteger(String key) {
        return retryGet(key, Integer.class);
    }

    public int getIntValue(String key, int defaultValue) {
        Integer value  = getInteger(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }


    public Boolean getBoolean(String key) {
        return retryGet(key, Boolean.class);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return retryGet(key, Boolean.class, defaultValue);
    }

    <T> T retryGet(String key, Class<T> clazz) {
        return retryGet(key, clazz, null);
    }

    <T> T retryGet(String key, Class<T> clazz, T defaultValue) {
        if (applicationConfiguration != null) {
            T t = applicationConfiguration.retryGet(key, clazz);
            if (t == null) {
                return defaultValue;
            }
        }
        final String toKey = toHyphenKey(key);
        final T value = getObject(toKey, clazz);
        if (value != null) {
            return value;
        }
        T object = this.getObject(key, clazz);
        if (object == null) {
            return defaultValue;
        }
        return object;
    }

    String toHyphenKey(String key) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, key);
    }

    <T> T getObject(String key, Class<T> clazz) {
        final String value = getStringValue(key);
        PropertyEditor editor = PropertyEditorManager.findEditor(clazz);
        editor.setAsText(value);
        return (T) editor.getValue();
    }

    public String getStringValue(String key) {
        return ServerEnvUtils.getProperty(key);
    }

    public String getApplicationName() {
        if (applicationConfiguration != null) {
            return applicationConfiguration.getApplicationName();
        }
        return getString("spring.application.name");
    }
}
