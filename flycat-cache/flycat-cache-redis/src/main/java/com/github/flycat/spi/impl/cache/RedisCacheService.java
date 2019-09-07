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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.flycat.spi.cache.CacheException;
import com.github.flycat.spi.cache.DistributedCacheService;
import com.github.flycat.spi.redis.RedisService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Singleton
@Named
public class RedisCacheService implements DistributedCacheService {

    public static final String CACHE_NULL = "CACHE_NULL_" + RedisCacheService.class.getName();

    private final RedisService redisService;

    @Inject
    public RedisCacheService(RedisService redisService) {
        this.redisService = redisService;
    }


    @Override
    public <T> Optional<T> queryNullableCacheObject(String module, String key,
                                                    Type type,
                                                    Callable<T> callable, int seconds)
            throws CacheException {
        try {
            String redisKey = "cache:" + module + ":" + key + ":" + seconds;
            final String cacheValue = redisService.get(redisKey);
            if (CACHE_NULL.equals(cacheValue)) {
                return Optional.empty();
            }
            T result;
            if (StringUtils.isBlank(cacheValue)) {
                result = callable.call();
                if (result == null) {
                    redisService.setex(redisKey, seconds, CACHE_NULL);
                } else {
                    final String jsonString = JSON.toJSONString(result);
                    redisService.setex(redisKey, seconds, jsonString);
                }
            } else {
                result = (T) JSON.parseObject(cacheValue, type);
            }
            return Optional.ofNullable(result);
        } catch (Exception e) {
            throw new CacheException("Unable to read cache from redis", e);
        }
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


    public final static Type LIST_INTEGER = new TypeReference<List<String>>() {
    }.getType();

    @Override
    public List<Integer> queryIntegerList(String module, String key, Callable<List<Integer>> callable, int seconds) {
        return queryCacheObject(module,
                key,
                LIST_INTEGER,
                () -> callable.call(), seconds
        );
    }
}
