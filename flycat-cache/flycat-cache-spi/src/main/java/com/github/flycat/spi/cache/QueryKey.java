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

import com.github.flycat.util.collection.StreamUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryKey<K> {
    private final String[] modules;
    private final List<K> keys;
    private final String keysString;

    public QueryKey(String[] modules, List<K> keys) {
        this.modules = modules;
        this.keys = keys;
        this.keysString = createKeysString(keys);
    }

    public String[] getModules() {
        return modules;
    }

    public static String getSubmoduleName(String module) {
        if (module.contains("#")) {
            return module.split("#")[1];
        }
        return module;
    }

    public String[] getSubmodules() {
        final ArrayList<String> submodules = new ArrayList<>();
        for (String module : modules) {
            submodules.add(getSubmoduleName(module));
        }
        return submodules.toArray(new String[]{});
    }

    public List<K> getKeys() {
        return keys;
    }

    public String getKeysString() {
        return keysString;
    }

    public static String createKeysString(List idList) {
        StringBuilder builder = new StringBuilder();
        if (idList == null || idList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < idList.size(); i++) {
            if (i == idList.size() - 1) {
                builder.append(idList.get(i));
            } else {
                builder.append(idList.get(i) + ",");
            }
        }
        return builder.toString();
    }

    public <K extends Number> Map<String, Map<String, K>> toMaps(List<Map<String, K>> mapList, String key) {
        return StreamUtils
                .toMaps(mapList, key, getModules(), getSubmodules());

    }
}
