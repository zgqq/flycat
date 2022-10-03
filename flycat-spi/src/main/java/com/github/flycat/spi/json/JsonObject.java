package com.github.flycat.spi.json;

import com.github.flycat.util.ValueUtils;

public interface JsonObject {


    JsonObject getJsonObject(String key);

    Integer getInteger(String key);

    String getString(String key);

    default Integer getInteger(String key, Integer defaultValue) {
        Integer value = getInteger(key);
        return ValueUtils.getValue(value, defaultValue);
    }
}
