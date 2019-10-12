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
package com.github.flycat.spi.impl.config;


import com.github.flycat.spi.json.JsonService;

public class RedisConfig {
    private final JsonService jsonService;
    private final String configKey;
    private final Object jsonObject;

    public RedisConfig(JsonService jsonService, String configKey, String json) {
        this.jsonService = jsonService;
        this.configKey = configKey;
        this.jsonObject = jsonService.parseTree(json);
    }

    public <T> T getConfigValue(String name, Class<T> clazz, T defaultValue) {
        Object customConfig = getCustomConfig();
        if (customConfig == null) {
            throw new RuntimeException("Not found config " + getConfigKey());
        }
        T object = jsonService.toObject(customConfig, name, clazz);
        if (object == null) {
            object = onNotFoundConfigValue(name, clazz);
        }
        if (object == null) {
            return defaultValue;
        }
        return object;
    }

    <T> T onNotFoundConfigValue(String name, Class<T> clazz) {
        return null;
    }

    private Object getCustomConfig() {
        return jsonObject;
    }

    public String getConfigKey() {
        return configKey;
    }

    public <T> T getConfigValue(String name, Class<T> clazz) {
        Object customConfig = getCustomConfig();
        if (customConfig == null) {
            throw new RuntimeException("Not found config " + getConfigKey());
        }
        T object = jsonService.toObject(customConfig, name, clazz);
        if (object == null) {
            object = onNotFoundConfigValue(name, clazz);
        }
        if (object == null) {
            throw new RuntimeException("Not found " + name + " in " + getCustomConfig());
        }
        return object;
    }

//    public <T> T getConfigValue(String name, Type typeReference) {
//        Object customConfig = getCustomConfig();
//        if (customConfig == null) {
//            throw new RuntimeException("Not found config " + getConfigKey());
//        }
//        T object = jsonService.toObject(customConfig, name, typeReference);
//        if (object == null) {
//            throw new RuntimeException("Not found " + name + " in " + getCustomConfig());
//        }
//        return object;
//    }

    public <T> T toType(Class<T> clazz) {
        final Object customConfig = getCustomConfig();
        return jsonService.toObject(customConfig, clazz);
    }
}
