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
