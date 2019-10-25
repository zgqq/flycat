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

    public Integer getInteger(String key) {
        return MapUtils.getInteger(map, key);
    }
}
