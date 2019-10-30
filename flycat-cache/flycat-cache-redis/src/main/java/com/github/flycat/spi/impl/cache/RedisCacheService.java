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
import com.github.flycat.spi.cache.CountMaps;
import com.github.flycat.spi.cache.DistributedCacheService;
import com.github.flycat.spi.cache.QueryKey;
import com.github.flycat.spi.json.JsonService;
import com.github.flycat.spi.redis.RedisOperations;
import com.github.flycat.spi.redis.RedisService;
import com.github.flycat.spi.redis.SessionCallback;
import com.github.flycat.util.StringUtils;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
    public <T> Optional<T> queryNullableCacheObject(String module, Object key,
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
                    redisService.setEx(redisKey, seconds, CACHE_NULL);
                } else {
                    final Stopwatch started = Stopwatch.createStarted();
                    final String jsonString = jsonService.toJsonString(result);
                    started.stop();
                    LOGGER.info("Serialized object to json, cost: {}", started);
                    redisService.execute(redisOperations -> {
                        redisOperations.multi();
                        redisOperations.setEx(redisKey, seconds, jsonString);
                        final String moduleKeys = createModuleKeys(module);
                        redisOperations.zAdd(moduleKeys, System.currentTimeMillis(), redisKey);
                        return redisOperations.exec();
                    });
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

    private String createCacheKey(String module, Object key) {
        return CACHE_REMOVABLE_PREFIX + module + ":" + key;
    }

    @Override
    public <T> Optional<T> queryNullableCacheObject(String module, Object key, Type type, Callable<T> callable)
            throws CacheException {
        return queryNullableCacheObject(module, key, type, callable, 300);
    }

    @Override
    public <T> T queryCacheObject(String module, Object key, Type type, Callable<T> callable) throws CacheException {
        return queryCacheObject(module, key, type, callable, 300);
    }

    @Override
    public <T> T queryCacheObject(String module, Object key, Type type, Callable<T> callable, int seconds)
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
    public Boolean removeCache(String module, String key) {
        final String cacheKey = createCacheKey(module, key);
        final Long del = redisService.del(cacheKey);
        if (del != null) {
            return del.intValue() > 0;
        }
        return null;
    }

    @Override
    public Boolean removeCache(String module) {
        final String moduleKeys = createModuleKeys(module);
        final Set<String> keys = redisService.zRange(moduleKeys, 0, -1);
        redisService.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) {
                redisOperations.del(moduleKeys);
                for (String key : keys) {
                    redisOperations.del(key);
                }
                return null;
            }
        });
        return false;
    }

    @Override
    public boolean isValueRefreshed(String module, Object key, int seconds) throws CacheException {
        final String redisKey = CACHE_REMOVABLE_PREFIX + "refresh:" + module + ":" + key;
        do {
            final boolean setnx = redisService.setNx(redisKey, (System.currentTimeMillis()
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

    @Override
    public long increaseCount(String module, Object key, Callable<Number> callable) throws CacheException {
        final String redisKey = CACHE_REMOVABLE_PREFIX + "count:" + module + ":" + key;
        try {
            final String count = redisService.get(redisKey);
            if (StringUtils.isNotBlank(count)) {
                return redisService.incr(redisKey);
            } else {
                final long call = callable.call().longValue();
                final boolean setnx = redisService.setNx(redisKey, call + "");
                if (setnx) {
                    return call;
                } else {
                    return redisService.incr(redisKey);
                }
            }
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    private String getCountKey(String module, Object key) {
        return CACHE_REMOVABLE_PREFIX + "count:" + module + ":" + key;
    }

    @Override
    public <T extends Number, K> CountMaps getCountMapsByModules(List<K> keys, Function<QueryKey<K>, Map<String, Map<String, T>>> callable, String... modules) throws CacheException {

        final ArrayList<Object> notFoundKeys = Lists.newArrayList();
        final Map<String, Map<String, T>> results = Maps.newHashMap();
        for (String module : modules) {
            final HashMap<String, T> result = Maps.newHashMap();
            for (Object key : keys) {
                final String countKey = getCountKey(module, key);
                final String value = redisService.get(countKey);
                if (value != null) {
                    result.put(key.toString(), (T) Long.valueOf(value));
                } else {
                    if (!notFoundKeys.contains(key)) {
                        notFoundKeys.add(key);
                    }
                }
            }
            results.put(module, result);
        }

        if (!notFoundKeys.isEmpty()) {
            final QueryKey<K> queryKey = new QueryKey<>(modules, keys);
            final Map<String, Map<String, T>> applyResults = callable.apply(queryKey);
            for (Map.Entry<String, Map<String, T>> stringMapEntry : applyResults.entrySet()) {
                final String module = stringMapEntry.getKey();
                final Map<String, T> computeResults = stringMapEntry.getValue();

                for (Map.Entry<String, T> numberEntry : computeResults.entrySet()) {
                    final Object key = numberEntry.getKey();
                    final Number value = numberEntry.getValue();
                    final String countKey = getCountKey(module, key);
                    redisService.setNx(countKey, value + "");
                }
                final Map<String, T> ktMap = results.get(module);
                if (ktMap == null) {
                    results.put(module, computeResults);
                } else {
                    ktMap.putAll(computeResults);
                }
            }
        }
        final CountMaps countMaps = new CountMaps(results);
        return countMaps;

    }
}
