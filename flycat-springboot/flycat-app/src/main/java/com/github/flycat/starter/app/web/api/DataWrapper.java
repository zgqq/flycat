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
