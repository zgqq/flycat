/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.spi.impl.cache;

import com.github.flycat.spi.cache.CacheException;
import com.github.flycat.spi.cache.StandaloneCacheService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.*;

@Singleton
@Named
public class GuavaCacheService implements StandaloneCacheService {

    private ConcurrentMap<String, Cache<Object, Object>> moduleMap = new ConcurrentHashMap();

    private static final Object NULL_OBJECT = new Object();

    @Override
    public <T> Optional<T> queryNullableCacheObject(String module, Object key,
                                                    Callable<T> callable) throws CacheException {
        try {
            Cache<Object, Object> cache = moduleMap.computeIfAbsent(module, (mapKey) -> {
                Cache<Object, Object> objectCache = CacheBuilder.newBuilder()
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
    public <T> T queryCacheObject(String module, Object key,
                                  Callable<T> callable) throws CacheException {
        try {
            Cache<Object, Object> cache = moduleMap.computeIfAbsent(module, (mapKey) -> {
                Cache<Object, Object> objectCache = CacheBuilder.newBuilder()
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
    public <T> T queryCacheObject(String module, Object key,
                                  Callable<T> callable,
                                  int seconds) throws CacheException {
        try {
            Cache<Object, Object> cache = createCache(module, seconds);
            return (T) cache.get(key, callable);
        } catch (ExecutionException e) {
            throw new CacheException("Guava cache error", e);
        }
    }

    private Cache<Object, Object> createCache(String module, int seconds) {
        return moduleMap
                .computeIfAbsent(module, (mapKey) -> {
                    Cache<Object, Object> objectCache = CacheBuilder.newBuilder()
                            .expireAfterWrite(seconds, TimeUnit.SECONDS)
                            .maximumSize(2048)
                            .build(); // look Ma, no CacheLoader
                    return objectCache;
                });
    }

    @Override
    public <T> T queryAllCacheObjects(String module, Callable<T> callable) throws CacheException {
        return queryCacheObject(module, "_ALL", callable);
    }

    @Override
    public boolean removeCache(String module, String key) {
        final Cache<Object, Object> cache = moduleMap.get(module);
        if (cache != null) {
            cache.invalidate(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeCache(String module) {
        moduleMap.remove(module);
        return true;
    }
}
