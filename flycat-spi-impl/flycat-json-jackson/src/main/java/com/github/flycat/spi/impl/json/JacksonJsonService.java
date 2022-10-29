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
package com.github.flycat.spi.impl.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.flycat.spi.json.JsonException;
import com.github.flycat.spi.json.JsonObject;
import com.github.flycat.spi.json.JsonService;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

@Named
@Singleton
public class JacksonJsonService implements JsonService {
    private TypeReference stringListType = new TypeReference<List<String>>() {
    };
    private final ObjectMapper objectMapper;

    @Inject
    public JacksonJsonService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static JsonService newInstance() {
        return new JacksonJsonService(new ObjectMapper());
    }

    @Override
    public boolean isJsonArray(String value) {
        try {
            return objectMapper.readTree(value).isArray();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Type createStringListReference() {
        return stringListType.getType();
    }

    @Override
    public Object getNode(Object jsonObject, String name) {
        if (jsonObject instanceof JsonNode) {
            JsonNode jsonNode = (JsonNode) jsonObject;
            return jsonNode.get(name);
        }
        throw new JsonException("Unsupported type, json:" + jsonObject + ", type:" + name);
    }

    @Override
    public Object parseTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> T parseObject(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> T parseObject(String json, Object type) {
        try {
            if (type instanceof Class) {
                return objectMapper.readValue(json, (Class<T>) type);
            } else if (type instanceof Type) {
                return objectMapper.readValue(json, objectMapper.getTypeFactory().constructType((Type) type));
            }
            throw new JsonException("Unsupported type, json:" + json + ", type:" + type);
        } catch (Throwable e) {
            throw new JsonException(e);
        }
    }

    @Override
    public String toJsonString(Object result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    @Override
    public <T> T toObject(Object jsonObject, String name, Class<T> clazz) {
        if (jsonObject instanceof JsonNode) {
            JsonNode object = (JsonNode) jsonObject;
            final JsonNode jsonNode = object.get(name);
            try {
                return objectMapper.treeToValue(jsonNode, clazz);
            } catch (JsonProcessingException e) {
                throw new JsonException(e);
            }
        }
        throw new JsonException("JsonObject is not a JsonNode " + jsonObject);
    }

    @Override
    public <T> T toObject(Object jsonObject, Class<T> clazz) {
        if (jsonObject instanceof JsonNode) {
            JsonNode object = (JsonNode) jsonObject;
            try {
                return objectMapper.treeToValue(object, clazz);
            } catch (JsonProcessingException e) {
                throw new JsonException(e);
            }
        }
        throw new JsonException("JsonObject is not a JsonNode " + jsonObject);
    }

    @Override
    public List<String> toStringList(String json) {
        try {
            return objectMapper.readValue(json, stringListType);
        } catch (Throwable e) {
            throw new JsonException(e);
        }
    }

    @Override
    public boolean isValidJson(String json) {
        try {
            final JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode != null;
        } catch (Throwable e) {
            return false;
        }
    }


    @Override
    public Object replaceArrayByMatchedObject(Object jsonObject, String arrayName, String bitKey, int matchBit) {
        ObjectNode jsonNode = (ObjectNode) jsonObject;
        final JsonNode jsonArray = jsonNode.get(arrayName);
        final Iterator<JsonNode> elements = jsonArray.elements();
        final ArrayNode jsonNodes = new ArrayNode(objectMapper.getNodeFactory());
        while (elements.hasNext()) {
            final JsonNode next = elements.next();
            final NumericNode jsonBit = (NumericNode) next.get(bitKey);
            if ((jsonBit.asInt() & matchBit) == matchBit) {
                jsonNodes.add(next);
            }
        }
        jsonNode.set(arrayName, jsonNodes);
        return jsonNode;
    }

    @Override
    public JsonObject parseJsonObject(String json) {
        JsonNode jsonNode = (JsonNode) parseTree(json);
        return new JsonObjectImpl(jsonNode, this);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
