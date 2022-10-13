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

import com.github.flycat.util.collection.StreamUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public interface CacheOperation {

    default String createModuleNameByStackTrace(Type type) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final StackTraceElement stackTraceElement = stackTrace[3];
        final String className = stackTraceElement.getClassName();
        final String methodName = stackTraceElement.getMethodName();
        String flag = className + "." + methodName;
        if (type != null) {
            flag += "-" + type.getTypeName();
        }
        return flag;
    }


    default Boolean removeCache(String module, String key) {
        throw new UnsupportedOperationException();
    }

    default Boolean removeCache(String module) {
        throw new UnsupportedOperationException();
    }


    default <P, T extends Number, K> CountMaps getCountMapsByModules(
            List<P> list, Function<? super P, K> mapper,
            Function<QueryKey<K>,
                    Map<String, Map<String, T>>>
                    callable,
            String... modules)
            throws CacheException {
        final List<K> ks = StreamUtils.mapList(list, mapper);
        return getCountMapsByModules(ks, callable, modules);
    }

    default <T extends Number, K> CountMaps getCountMapsByModules(List<K> keys,
                                                                  Function<QueryKey<K>,
                                                                          Map<String, Map<String, T>>>
                                                                          callable,
                                                                  String... modules)
            throws CacheException {
        throw new UnsupportedOperationException();
    }

    default long increaseCount(String module, Object key) throws CacheException {
        return increaseCount(module, key, new Callable<Number>() {
            @Override
            public Number call() throws Exception {
                return 1;
            }
        });
    }


    default long increaseCount(String module, Object key, Callable<Number> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }


    default <T> T queryCacheObject(String module, Object key,
                                   Type type,
                                   Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> T queryCacheObject(String module, Object key,
                                   Type type,
                                   Callable<T> callable,
                                   int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }


    default <T> T queryAllCacheObjects(String module,
                                       Type type,
                                       Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }


    default <T> T queryCacheObject(Object key,
                                   Type type,
                                   Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }


    default <T> Optional<T> queryNullableCacheObject(String module, Object key,
                                                     Callable<T> callable, int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> Optional<T> queryNullableCacheObject(String module, Object key,
                                                     Callable<T> callable) throws CacheException {
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

    default boolean isValueRefreshed(String module, Object key,
                                     int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> T queryCacheObject(String module, Object key,
                                   Callable<T> callable,
                                   int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> ExecuteResult<T> executeOnceAction(String module,
                                                   Object key,
                                                   Callable<T> callable,
                                                   int seconds) {

        AtomicBoolean newValue = new AtomicBoolean(false);
        T returnValue = queryCacheObject(module, key,
                () -> {
                    T call = callable.call();
                    newValue.set(true);
                    return call;
                }, seconds
        );
        ExecuteResult<T> result = new ExecuteResult<>(newValue.get(), returnValue);
        return result;
    }
}
