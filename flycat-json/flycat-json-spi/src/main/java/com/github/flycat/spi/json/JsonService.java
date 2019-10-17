package com.github.flycat.spi.json;

import java.lang.reflect.Type;
import java.util.List;

public interface JsonService {

    boolean isJsonArray(String value);

    Type createStringListReference();

    Object getNode(Object jsonObject, String name);

    Object parseTree(String json);

    <T> T parseObject(String json, Class<T> type);

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
}
