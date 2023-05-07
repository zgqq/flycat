package com.github.flycat.spi.impl.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.flycat.spi.json.JsonArray;
import com.github.flycat.spi.json.JsonException;
import com.github.flycat.spi.json.JsonObject;

public class JsonObjectImpl implements JsonObject {
    private final JsonNode jsonNode;
    private final JacksonJsonService jsonService;

    public JsonObjectImpl(JsonNode jsonNode, JacksonJsonService jsonService) {
        this.jsonNode = jsonNode;
        this.jsonService = jsonService;
    }

    @Override
    public JsonObject getJsonObject(String key) {
        return new JsonObjectImpl(jsonNode.get(key), jsonService);
    }

    @Override
    public <T> T getJsonObject(String key, Class<T> clazz) {
        JsonNode jsonNode1 = jsonNode.get(key);
        ObjectMapper objectMapper = jsonService.getObjectMapper();
        try {
            return objectMapper.treeToValue(jsonNode1, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer getInteger(String key) {
        JsonNode jsonNode = this.jsonNode.get(key);
        if (jsonNode == null) {
            return null;
        }
        return jsonNode.asInt();
    }

    @Override
    public Long getLong(String key) {
        JsonNode jsonNode = this.jsonNode.get(key);
        if (jsonNode == null) {
            return null;
        }
        return jsonNode.asLong();
    }

    @Override
    public String getString(String key) {
        JsonNode jsonNode = this.jsonNode.get(key);
        if (jsonNode == null) {
            return null;
        }
        return jsonNode.asText();
    }

    @Override
    public Double getDouble(String key) {
        JsonNode jsonNode = this.jsonNode.get(key);
        if (jsonNode == null) {
            return null;
        }
        return jsonNode.asDouble();
    }

    @Override
    public JsonArray getJsonArray(String key) {
        JsonNode jsonNode = this.jsonNode.get(key);
        if (jsonNode == null) {
            return null;
        }
        if (!jsonNode.isArray()) {
            throw new JsonException("key " + key + " is not a array");
        }
        return new JsonArrayImpl(jsonNode, jsonService);
    }

    @Override
    public <T> T toObject(Class<T> clazz) {
        ObjectMapper objectMapper = jsonService.getObjectMapper();
        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
