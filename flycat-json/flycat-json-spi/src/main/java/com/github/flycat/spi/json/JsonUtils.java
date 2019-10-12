package com.github.flycat.spi.json;

import com.github.flycat.context.ContextUtils;

public class JsonUtils {
    private static volatile JsonService jsonService;

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
        return jsonService;
    }

    public static boolean isJsonArray(String json) {
        return getJsonService().isJsonArray(json);
    }
}
