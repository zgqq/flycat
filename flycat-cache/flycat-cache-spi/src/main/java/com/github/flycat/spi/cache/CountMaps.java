package com.github.flycat.spi.cache;

import com.github.flycat.util.NumberUtils;

import java.util.Map;

public class CountMaps<K> {
    private final Map<String, Map<K, ? extends Number>> maps;

    public CountMaps(Map<String, Map<K, ? extends Number>> maps) {
        this.maps = maps;
    }

    public Integer getInteger(String module, Object id) {
        final Object o = maps.get(module).get(id);
        return NumberUtils.toInteger(o);
    }
}
