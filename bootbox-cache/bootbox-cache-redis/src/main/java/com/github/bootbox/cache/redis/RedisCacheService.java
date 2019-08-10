package com.github.bootbox.cache.redis;

import com.alibaba.fastjson.JSON;
import com.github.bootbox.cache.CacheException;
import com.github.bootbox.cache.DistributedCacheService;
import com.github.bootbox.redis.RedisService;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.Callable;

public class RedisCacheService implements DistributedCacheService {

    public static final String CACHE_NULL = "CACHE_NULL_" + RedisCacheService.class.getName();

    private final RedisService redisService;

    public RedisCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public <T> Optional<T> queryNullableCacheString(String module, String key, Callable<T> callable, int seconds)
            throws CacheException {
        try {
            String redisKey = module + "_" + key + "_" + seconds;
            final String cacheValue = redisService.get(redisKey);
            if (CACHE_NULL.equals(cacheValue)) {
                return Optional.empty();
            }
            T result;
            if (StringUtils.isBlank(cacheValue)) {
                result = callable.call();
                if (result == null) {
                    redisService.setex(redisKey, CACHE_NULL, seconds);
                } else {
                    final String jsonString = JSON.toJSONString(result);
                    redisService.setex(redisKey, jsonString, seconds);
                }
            } else {
                result = (T) JSON.parse(cacheValue);
            }
            return Optional.ofNullable(result);
        } catch (Exception e) {
            throw new CacheException("Unable to read cache from redis", e);
        }
    }

    @Override
    public <T> Optional<T> queryNullableCacheString(String module, String key, Callable<T> callable)
            throws CacheException {
        return queryNullableCacheString(module, key, callable, 300);
    }

    @Override
    public <T> T queryCacheString(String module, String key, Callable<T> callable) throws CacheException {
        return queryCacheString(module, key, callable, 300);
    }

    @Override
    public <T> T queryCacheString(String module, String key, Callable<T> callable, int seconds)
            throws CacheException {
        return queryNullableCacheString(module, key, callable, seconds).orElseThrow(() -> new CacheException(
                "Cache value is null, module:" + module + ", key:" + key));
    }

    @Override
    public <T> T queryAllCacheString(String module, Callable<T> callable) throws CacheException {
        return queryCacheString(module, "ALL", callable);
    }
}
