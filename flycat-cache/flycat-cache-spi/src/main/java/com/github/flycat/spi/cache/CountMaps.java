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
package com.github.flycat.spi.cache;

import com.github.flycat.util.NumberUtils;

import java.util.Map;

public class CountMaps<K> {
    private final Map<String, Map<K, ? extends Number>> maps;

    public CountMaps(Map<String, Map<K, ? extends Number>> maps) {
        this.maps = maps;
    }

    public Integer getInteger(String module, Object id) {
        final String moduleName = QueryKey.getSubmoduleName(module);
        final Object o = maps.get(moduleName).get(id.toString());
        return NumberUtils.toInteger(o);
    }
}
