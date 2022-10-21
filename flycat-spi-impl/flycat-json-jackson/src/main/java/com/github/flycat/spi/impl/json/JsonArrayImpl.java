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
    private final JacksonJsonService jsonService;

    public JsonArrayImpl(JsonNode jsonNode, JacksonJsonService jsonService) {
        this.jsonNode = jsonNode;
        this.jsonService = jsonService;
    }

    @Override
    public JsonObject getJsonObject(int index) {
        return new JsonObjectImpl(jsonNode.get(index), jsonService);
    }

    @Override
    public Iterator<? extends JsonObject> iterator() {
        Iterator<JsonNode> iterator = jsonNode.iterator();
        return Lists.newArrayList(iterator)
                .stream()
                .map(obj -> new JsonObjectImpl(obj, jsonService))
                .collect(Collectors.toList()).iterator();
    }
}
