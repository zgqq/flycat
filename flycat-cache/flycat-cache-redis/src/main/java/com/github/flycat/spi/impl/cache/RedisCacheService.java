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
import com.github.flycat.spi.cache.DistributedCacheService;
import com.github.flycat.spi.json.JsonService;
import com.github.flycat.spi.redis.RedisService;
import com.github.flycat.util.StringUtils;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Singleton
@Named
public class RedisCacheService implements DistributedCacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheService.class);

    public static final String CACHE_NULL = "CACHE_NULL_" + RedisCacheService.class.getName();

    private final RedisService redisService;
    private final JsonService jsonService;
    private final Type stringListType;

    @Inject
    public RedisCacheService(RedisService redisService, JsonService jsonService) {
        this.redisService = redisService;
        this.jsonService = jsonService;
        this.stringListType = jsonService.createStringListReference();
    }


    @Override
    public <T> Optional<T> queryNullableCacheObject(String module, String key,
                                                    Type type,
                                                    Callable<T> callable, int seconds)
            throws CacheException {
        String cacheValue = null;
        try {
            String redisKey = createCacheKey(module, key);
            cacheValue = redisService.get(redisKey);
            if (CACHE_NULL.equals(cacheValue)) {
                return Optional.empty();
            }
            T result;
            if (StringUtils.isBlank(cacheValue)) {
                result = callable.call();
                if (result == null) {
                    redisService.setex(redisKey, seconds, CACHE_NULL);
                } else {
                    final Stopwatch started = Stopwatch.createStarted();
                    final String jsonString = jsonService.toJsonString(result);
                    started.stop();
                    LOGGER.info("Serialized object to json, cost: {}", started);
                    redisService.setex(redisKey, seconds, jsonString);
                    final String moduleKeys = createModuleKeys(module);
                    redisService.zadd(moduleKeys, System.currentTimeMillis(), redisKey);
                }
            } else {
                result = jsonService.parseObject(cacheValue, type);
            }
            return Optional.ofNullable(result);
        } catch (Exception e) {
            throw new CacheException("Unable to read cache from redis, cacheValue " + cacheValue, e);
        }
    }

    private String createModuleKeys(String module) {
        return CACHE_REMOVABLE_PREFIX + "keys:" + module;
    }

    private String createCacheKey(String module, String key) {
        return CACHE_REMOVABLE_PREFIX + module + ":" + key;
    }

    @Override
    public <T> Optional<T> queryNullableCacheObject(String module, String key, Type type, Callable<T> callable)
            throws CacheException {
        return queryNullableCacheObject(module, key, type, callable, 300);
    }

    @Override
    public <T> T queryCacheObject(String module, String key, Type type, Callable<T> callable) throws CacheException {
        return queryCacheObject(module, key, type, callable, 300);
    }

    @Override
    public <T> T queryCacheObject(String module, String key, Type type, Callable<T> callable, int seconds)
            throws CacheException {
        return queryNullableCacheObject(module, key, type, callable, seconds).orElseThrow(() -> new CacheException(
                "Cache value is null, module:" + module + ", key:" + key));
    }

    @Override
    public <T> T queryAllCacheObjects(String module, Type returnType, Callable<T> callable) throws CacheException {
        return queryCacheObject(module, "ALL", returnType, callable);
    }

    @Override
    public List<Integer> queryIntegerList(String module, String key, Callable<List<Integer>> callable, int seconds) {
        return queryCacheObject(module,
                key,
                stringListType,
                () -> callable.call(), seconds
        );
    }

    @Override
    public <T> T queryCacheObject(Object key, Type type, Callable<T> callable) throws CacheException {
        final String module = createModuleNameByStackTrace(type);
        return queryCacheObject(module, key + "", type, callable);
    }

    @Override
    public boolean removeCache(String module, String key) {
        final String cacheKey = createCacheKey(module, key);
        final Long del = redisService.del(cacheKey);
        return del.intValue() > 0;
    }

    @Override
    public boolean removeCache(String module) {
        final String moduleKeys = createModuleKeys(module);
        final Set<String> keys = redisService.zrange(moduleKeys, 0, -1);
        redisService.del(moduleKeys);
        for (String key : keys) {
            redisService.del(key);
        }
        return false;
    }

    @Override
    public boolean isValueRefreshed(String module, Object key, int seconds) throws CacheException {
        final String redisKey = CACHE_REMOVABLE_PREFIX + "refresh:" + module + ":" + key;
        do {
            final boolean setnx = redisService.setnx(redisKey, (System.currentTimeMillis()
                    + TimeUnit.SECONDS.toMillis(seconds)) + "");
            if (setnx) {
                redisService.expire(redisKey, seconds);
                return true;
            } else {
                final String value = redisService.get(redisKey);
                if (value != null) {
                    final long l = Long.parseLong(value);
                    if (System.currentTimeMillis() > l) {
                        redisService.del(redisKey);
                    } else {
                        return false;
                    }
                }
            }
        } while (true);
    }
}
