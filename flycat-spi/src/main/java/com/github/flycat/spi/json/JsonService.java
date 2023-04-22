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

import java.lang.reflect.Type;
import java.util.List;

public interface JsonService {

    SerializationConfig getSerializationConfig();

    boolean isJsonArray(String value);

    Type createStringListReference();

    Object getNode(Object jsonObject, String name);

    Object parseTree(String json);

    <T> T parseObject(String json, Class<T> type);

    @SuppressWarnings("TypeParameterUnusedInFormals")
    <T> T parseObject(String json, Object type);

    String toJsonString(Object result);

    <T> T toObject(Object jsonObject, String name, Class<T> clazz);

    <T> T toObject(Object jsonObject, Class<T> clazz);

    List<String> toStringList(String value);

    boolean isValidJson(String json);

    Object replaceArrayByMatchedObject(
            Object jsonObject,
            String arrayName,
            String bitKey,
            int matchBit
    );

    JsonObject parseJsonObject(String json);
}
