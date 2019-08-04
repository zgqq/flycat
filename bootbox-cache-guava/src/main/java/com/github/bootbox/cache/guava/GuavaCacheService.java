package com.github.bootbox.cache.guava;

import com.github.bootbox.cache.CacheException;
import com.github.bootbox.cache.StandaloneCacheService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Optional;
import java.util.concurrent.*;

public class GuavaCacheService implements StandaloneCacheService {

    private ConcurrentMap moduleMap = new ConcurrentHashMap();

    private static final Object NULL_OBJECT = new Object();

    @Override
    public <T> Optional<T> queryNullableCacheString(String module, String key,
                                                    Callable<T> callable) throws CacheException {
        try {
            Cache<String, Object> cache = (Cache<String, Object>) moduleMap.computeIfAbsent(module, (mapKey) -> {
                Cache<String, Object> objectCache = CacheBuilder.newBuilder()
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .maximumSize(2048)
                        .build(); // look Ma, no CacheLoader
                return objectCache;
            });
            T t = (T) cache.get(key, () -> {
                T call = callable.call();
                if (call == null) {
                    return NULL_OBJECT;
                }
                return call;
            });
            return Optional.ofNullable(t);
        } catch (Exception e) {
            throw new CacheException("Guava cache error", e);
        }
    }

    @Override
    public <T> T queryCacheString(String module, String key,
                                  Callable<T> callable) throws CacheException {
        try {
            Cache<String, Object> cache = (Cache<String, Object>) moduleMap.computeIfAbsent(module, (mapKey) -> {
                Cache<String, Object> objectCache = CacheBuilder.newBuilder()
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .maximumSize(2048)
                        .build(); // look Ma, no CacheLoader
                return objectCache;
            });
            return (T) cache.get(key, callable);
        } catch (Exception e) {
            throw new CacheException("Guava cache error", e);
        }
    }

    @Override
    public <T> T queryCacheString(String module, String key,
                                  Callable<T> callable,
                                  int seconds) throws CacheException {
        try {
            Cache<String, Object> cache = (Cache<String, Object>) moduleMap
                    .computeIfAbsent(module + "_" + seconds, (mapKey) -> {
                        Cache<String, Object> objectCache = CacheBuilder.newBuilder()
                                .expireAfterWrite(seconds, TimeUnit.SECONDS)
                                .maximumSize(2048)
                                .build(); // look Ma, no CacheLoader
                        return objectCache;
                    });
            return (T) cache.get(key, callable);
        } catch (ExecutionException e) {
            throw new CacheException("Guava cache error", e);
        }
    }

    @Override
    public <T> T queryAllCacheString(String module, Callable<T> callable) throws CacheException {
        return queryCacheString(module, "_ALL", callable);
    }
}
