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

    default boolean removeCache(String module, String key) {
        throw new UnsupportedOperationException();
    }

    default boolean removeCache(String module) {
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
}
