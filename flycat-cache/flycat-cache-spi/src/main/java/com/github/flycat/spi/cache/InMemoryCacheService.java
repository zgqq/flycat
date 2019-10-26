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
package com.github.flycat.spi.cache;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.Callable;

public interface InMemoryCacheService extends CacheOperation {

    default <T> Optional<T> queryNullableCacheObject(String module, Object key,
                                                     Callable<T> callable, int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> Optional<T> queryNullableCacheObject(String module, Object key,
                                                     Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> T queryCacheObject(String module, Object key,
                                   Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> T queryCacheObject(String module, Object key,
                                   Callable<T> callable,
                                   int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }


    default <T> T queryAllCacheObjects(String module,
                                       Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }


    default boolean isValueRefreshed(String module, Object key,
                                     int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }


    default <T> Optional<T> queryNullableCacheObject(String module, String key,
                                                     Type type,
                                                     Callable<T> callable, int seconds) throws CacheException {
        return queryNullableCacheObject(module, key, callable, seconds);
    }

    default <T> Optional<T> queryNullableCacheObject(String module, String key,
                                                     Type type,
                                                     Callable<T> callable) throws CacheException {
        return queryNullableCacheObject(module, key, callable);
    }

    default <T> T queryCacheObject(String module, Object key,
                                   Type type,
                                   Callable<T> callable) throws CacheException {
        return queryCacheObject(module, key, callable);
    }

    default <T> T queryCacheObject(String module, Object key,
                                   Type type,
                                   Callable<T> callable,
                                   int seconds) throws CacheException {
        return queryCacheObject(module, key, callable, seconds);
    }


    default <T> T queryAllCacheObjects(String module,
                                       Type type,
                                       Callable<T> callable) throws CacheException {
        return queryAllCacheObjects(module, callable);
    }


    default <T> T queryCacheObject(Object key,
                                   Type type,
                                   Callable<T> callable) throws CacheException {
        return queryCacheObject(key, callable);
    }

    default <T> T queryCacheObject(Object key,
                                   Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }
}
