package com.github.flycat.spi.impl.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.flycat.spi.json.JsonArray;
import com.github.flycat.spi.json.JsonException;
import com.github.flycat.spi.json.JsonObject;

public class JsonObjectImpl implements JsonObject {
    private final JsonNode jsonNode;

    public JsonObjectImpl(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    @Override
    public JsonObject getJsonObject(String key) {
        return new JsonObjectImpl(jsonNode.get(key));
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
        return new JsonArrayImpl(jsonNode);
    }
}
