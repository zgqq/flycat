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
package com.github.flycat.spi.json;

import com.github.flycat.context.ContextUtils;

import java.lang.reflect.Method;

public class JsonUtils {
    private static volatile JsonService jsonService;
    private static volatile JsonService defaultJsonService;

    public static String toJsonString(Object object) {
        final JsonService jsonService = getJsonService();
        return jsonService.toJsonString(object);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        final JsonService jsonService = getJsonService();
        return jsonService.parseObject(json, clazz);
    }

    private static JsonService getJsonService() {
        if (jsonService == null) {
            jsonService = ContextUtils.getBean(JsonService.class);
        }
        if (jsonService == null) {
            if (defaultJsonService == null) {
                try {
                    final Class<?> aClass = Class.forName("com.github.flycat.spi.impl.json.JacksonJsonService");
                    final Method newInstance = aClass.getMethod("newInstance");
                    defaultJsonService = (JsonService) newInstance.invoke(null);
                } catch (Throwable e) {
                    throw new JsonException(e);
                }
            }
            return defaultJsonService;
        }
        return jsonService;
    }

    public static boolean isJsonArray(String json) {
        return getJsonService().isJsonArray(json);
    }
}
