package com.github.flycat.starter.app.web.api;

import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.util.MapWrapper;

import java.util.Map;

public class DataWrapper extends MapWrapper {

    public DataWrapper(Map map) {
        super(map);
    }

    public <T> T toClass(Class<T> clazz) {
        final Map dataMap = getMap();
        T instance = null;
        if (dataMap == null || dataMap.isEmpty()) {
            try {
                instance = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to new instance", e);
            }
        } else {
            String json = JsonUtils.toJsonString(dataMap);
            instance = JsonUtils.parseObject(json, clazz);
        }
        return instance;
    }
}
