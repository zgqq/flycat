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

import com.github.flycat.spi.cache.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Singleton
@Named
public class GuavaCacheService implements StandaloneCacheService {

    private ConcurrentMap<String, Cache<Object, Object>> moduleMap = new ConcurrentHashMap();
    private ConcurrentMap<String, AtomicLong> atomicLongMap = new ConcurrentHashMap();

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
    public Boolean removeCache(String module, String key) {
        final Cache<Object, Object> cache = moduleMap.get(module);
        if (cache != null) {
            cache.invalidate(key);
            return true;
        }
        return false;
    }

    @Override
    public Boolean removeCache(String module) {
        moduleMap.remove(module);
        return true;
    }

    @Override
    public boolean isValueRefreshed(String module, Object key, int seconds) throws CacheException {
        final Cache<Object, Object> cache = createCache(module, seconds);
        final Object o = new Object();
        try {
            return o == cache.get(key, () -> o);
        } catch (ExecutionException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public <T> T queryCacheObject(Object key, Callable<T> callable) throws CacheException {
        return queryCacheObject(createModuleNameByStackTrace(null), key, callable);
    }


    @Override
    public long getCount(String module, Object key, Callable<Number> callable) throws CacheException {
        final String countKey = getCountKey(module, key);
        final AtomicLong atomicLong = atomicLongMap.get(countKey);
        if (atomicLong != null) {
            return atomicLong.get();
        }
        final Number call;
        try {
            call = callable.call();
        } catch (Exception e) {
            throw new CacheException(e);
        }
        final long initialValue = call.longValue();
        final AtomicLong prevAtomicLong = atomicLongMap.putIfAbsent(countKey,
                new AtomicLong(initialValue)
        );
        if (prevAtomicLong != null) {
            return prevAtomicLong.get();
        }
        return initialValue;
    }

    private String getCountKey(String module, Object key) {
        return module + ":" + key;
    }

    @Override
    public long increaseCount(String module, Object key, Callable<Number> callable) throws CacheException {
        try {
            final String countKey = getCountKey(module, key);
            final AtomicLong atomicLong = atomicLongMap.get(countKey);
            if (atomicLong == null) {
                final Long call = callable.call().longValue();
                final AtomicLong prevAtomicLong = atomicLongMap.putIfAbsent(countKey,
                        new AtomicLong(call)
                );
                if (prevAtomicLong != null) {
                    return prevAtomicLong.incrementAndGet();
                } else {
                    return call.intValue() + 1;
                }
            } else {
                return atomicLong.incrementAndGet();
            }
        } catch (Exception e) {
            throw new CacheException("Unable to increase count", e);
        }
    }


    @Override
    public <T extends Number, K> Map<K, T> getCountMap(String module, List<K> keys,
                                                       Function<List<K>, Map<K, T>> callable) throws CacheException {
        final HashMap<Object, Number> result = Maps.newHashMap();
        final ArrayList<Object> notFoundKeys = Lists.newArrayList();
        for (Object key : keys) {
            final String countKey = getCountKey(module, key);
            final AtomicLong atomicLong = atomicLongMap.get(countKey);
            if (atomicLong != null) {
                final long l = atomicLong.get();
                result.put(key, l);
            } else {
                notFoundKeys.add(key);
            }
        }
        final Map<Object, Number> computeResults = (Map<Object, Number>) callable.apply((List<K>) notFoundKeys);
        for (Map.Entry<Object, Number> numberEntry : computeResults.entrySet()) {
            final Object key = numberEntry.getKey();
            final Number value = numberEntry.getValue();
            final String countKey = getCountKey(module, key);
            atomicLongMap.putIfAbsent(countKey,
                    new AtomicLong((Long) value));
        }
        result.putAll(computeResults);

        return (Map<K, T>) result;
    }

    @Override
    public <T extends Number, K> CountMaps getCountMapsByModules(
            List<K> keys,
            Function<QueryKey<K>,
                    Map<String, Map<String, T>>> callable, String... modules)
            throws CacheException {
        final ArrayList<Object> notFoundKeys = Lists.newArrayList();
        final Map<String, Map<String, T>> results = Maps.newHashMap();
        for (String module : modules) {
            final HashMap<String, T> result = Maps.newHashMap();
            for (Object key : keys) {
                final String countKey = getCountKey(module, key);
                final AtomicLong atomicLong = atomicLongMap.get(countKey);
                if (atomicLong != null) {
                    final Long l = atomicLong.get();
                    result.put(key.toString(), (T) l);
                } else {
                    if (!notFoundKeys.contains(key)) {
                        notFoundKeys.add(key);
                    }
                }
            }
            results.put(module, result);
        }

        if (!notFoundKeys.isEmpty()) {
            final Map<String, Map<String, T>> applyResults = callable.apply(new QueryKey<K>(modules,
                    (List<K>) notFoundKeys));
            for (Map.Entry<String, Map<String, T>> stringMapEntry : applyResults.entrySet()) {
                final String module = stringMapEntry.getKey();
                final Map<String, T> computeResults = stringMapEntry.getValue();

                for (Map.Entry<String, T> numberEntry : computeResults.entrySet()) {
                    final Object key = numberEntry.getKey();
                    final Number value = numberEntry.getValue();
                    final String countKey = getCountKey(module, key);
                    atomicLongMap.putIfAbsent(countKey,
                            new AtomicLong(value.longValue()));
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

    @Override
    public <T> ExecuteResult<T> executeOnceAction(String module, Object key,
                                                  Callable<T> callable, int seconds) {
        AtomicBoolean newValue = new AtomicBoolean(false);
        T returnValue = queryCacheObject(module, key,
                () -> {
                    T call = callable.call();
                    newValue.set(true);
                    return call;
                }, 3600 * 24
        );
        ExecuteResult<T> result = new ExecuteResult<>(newValue.get(), returnValue);
        return result;
    }
}
