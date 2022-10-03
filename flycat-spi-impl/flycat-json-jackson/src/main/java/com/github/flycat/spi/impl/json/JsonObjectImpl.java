package com.github.flycat.spi.impl.json;

import com.fasterxml.jackson.databind.JsonNode;
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
    public String getString(String key) {
        JsonNode jsonNode = this.jsonNode.get(key);
        if (jsonNode == null) {
            return null;
        }
        return jsonNode.asText();
    }
}
