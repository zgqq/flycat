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
