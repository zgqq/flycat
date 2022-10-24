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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public interface DistributedCacheService extends CacheOperation {

    String CACHE_REMOVABLE_PREFIX = "cache:removable:";

    default <T> Optional<T> queryNullableCacheObject(String module, Object key,
                                                     Type type,
                                                     Callable<T> callable, int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> Optional<T> queryNullableCacheObject(String module, Object key,
                                                     Class<T> type,
                                                     Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }


    List<Integer> queryIntegerList(String module, String key, Callable<List<Integer>> callable, int seconds);
//    default <T extends Number, K> CountMaps getCountMapsByModules(List<String> modules,
//                                                                  List<K> keys,
//                                                                  Function<List<K>,
//                                                                          Map<String, Map<K, T>>>
//                                                                          callable)
//            throws CacheException {
//        throw new UnsupportedOperationException();
//    }


//    <T> Optional<T> queryNullableCacheObject(String module, String key,
//                                                     Class<T> type,
//                                                     Callable<T> callable, int seconds);

    <T> Optional<T> queryNullableCacheObject(String module, String key,
                                             Class<T> type,
                                             Callable<T> callable) throws CacheException;


    default <T> ExecuteResult<T> executeOnceAction(String module,
                                                   Object key,
                                                   Class<T> type,
                                                   Callable<T> callable,
                                                   int seconds) {
        AtomicBoolean newValue = new AtomicBoolean(false);
        T returnValue = queryCacheObject(module, key, type,
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
