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
package com.github.bootbox.config.redis;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public class RedisConfig {
    private final String configKey;
    private final JSONObject jsonObject;

    public RedisConfig(String configKey, JSONObject jsonObject) {
        this.configKey = configKey;
        this.jsonObject = jsonObject;
    }

    public <T> T getConfigValue(String name, Class<T> clazz, T defaultValue) {
        JSONObject customConfig = getCustomConfig();
        if (customConfig == null) {
            throw new RuntimeException("Not found config " + getConfigKey());
        }
        T object = customConfig.getObject(name, clazz);
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

    private JSONObject getCustomConfig() {
        return jsonObject;
    }

    public String getConfigKey() {
        return configKey;
    }

    public <T> T getConfigValue(String name, Class<T> clazz) {
        JSONObject customConfig = getCustomConfig();
        if (customConfig == null) {
            throw new RuntimeException("Not found config " + getConfigKey());
        }
        T object = customConfig.getObject(name, clazz);
        if (object == null) {
            object = onNotFoundConfigValue(name, clazz);
        }
        if (object == null) {
            throw new RuntimeException("Not found " + name + " in " + getCustomConfig());
        }
        return object;
    }

    public <T> T getConfigValue(String name, TypeReference typeReference) {
        JSONObject customConfig = getCustomConfig();
        if (customConfig == null) {
            throw new RuntimeException("Not found config " + getConfigKey());
        }
        T object = customConfig.getObject(name, typeReference);
        if (object == null) {
            throw new RuntimeException("Not found " + name + " in " + getCustomConfig());
        }
        return object;
    }

    public <T> T toType(Class<T> clazz) {
        final JSONObject customConfig = getCustomConfig();
        return customConfig.toJavaObject(clazz);
    }
}
