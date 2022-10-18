package com.github.flycat.spi.impl.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.flycat.spi.json.JsonArray;
import com.github.flycat.spi.json.JsonObject;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class JsonArrayImpl implements JsonArray {

    private final JsonNode jsonNode;

    public JsonArrayImpl(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    @Override
    public JsonObject getJsonObject(int index) {
        return new JsonObjectImpl(jsonNode.get(index));
    }

    @Override
    public Iterator<? extends JsonObject> iterator() {
        Iterator<JsonNode> iterator = jsonNode.iterator();
        return Lists.newArrayList(iterator)
                .stream()
                .map(obj -> new JsonObjectImpl(obj))
                .collect(Collectors.toList()).iterator();
    }
}
