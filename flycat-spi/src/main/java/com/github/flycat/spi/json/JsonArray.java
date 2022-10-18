package com.github.flycat.spi.json;

import java.util.Iterator;

public interface JsonArray {
    JsonObject getJsonObject(int index);

    Iterator<? extends JsonObject> iterator();
}
