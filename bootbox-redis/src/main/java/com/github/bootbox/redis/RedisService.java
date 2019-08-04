package com.github.bootbox.redis;

import java.util.Map;
import java.util.Set;

public interface RedisService {
    String hget(String redisKey, String key);

    Set<String> smembers(String key);

    String get(String key);

    Map<String, String> hgetAll(String key);

    void hsetAsJson(String key, String hashKey, Object object);

    void setex(String key, String value, long seconds);

    <T> T getJsonObject(String key, Class<T> clazz);

    void setexAsJson(String key, Object object, long seconds);
}
