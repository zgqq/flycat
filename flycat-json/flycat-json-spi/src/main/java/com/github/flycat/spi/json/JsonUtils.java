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
