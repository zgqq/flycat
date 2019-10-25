package com.github.flycat.util;


import org.apache.commons.collections.MapUtils;

import java.util.Map;

public class MapWrapper {
    private final Map map;

    public MapWrapper(Map map) {
        this.map = map;
    }

    public String getString(String key) {
        return MapUtils.getString(map, key);
    }

    public String getString(Object key, String defaultValue) {
        return MapUtils.getString(map, key, defaultValue);
    }

    public Integer getInteger(String key) {
        return MapUtils.getInteger(map, key);
    }

    public Integer getInteger(Object key, Integer defaultValue) {
        return MapUtils.getInteger(map, key, defaultValue);
    }

    public Map getMap() {
        return map;
    }
}
