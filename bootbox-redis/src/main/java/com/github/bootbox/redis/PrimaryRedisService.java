package com.github.bootbox.redis;

import java.util.Map;
import java.util.Set;

public class PrimaryRedisService implements RedisService {

    private final RedisService provider;

    public PrimaryRedisService(RedisService provider) {
        this.provider = provider;
    }

    @Override
    public String hget(String redisKey, String key) {
        return provider.hget(redisKey, key);
    }

    @Override
    public Set<String> smembers(String key) {
        return provider.smembers(key);
    }

    @Override
    public String get(String key) {
        return provider.get(key);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return provider.hgetAll(key);
    }

    @Override
    public void hsetAsJson(String key, String hashKey, Object object) {
        provider.hsetAsJson(key, hashKey, object);
    }

    @Override
    public void setex(String key, String value, long seconds) {
        provider.setex(key, value, seconds);
    }

    @Override
    public <T> T getJsonObject(String key, Class<T> clazz) {
        return provider.getJsonObject(key, clazz);
    }

    @Override
    public void setexAsJson(String key, Object object, long seconds) {
        provider.setexAsJson(key, object, seconds);
    }
}
