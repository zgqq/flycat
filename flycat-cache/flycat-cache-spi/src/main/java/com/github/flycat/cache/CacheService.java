/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.cache;

import java.util.Optional;
import java.util.concurrent.Callable;

public interface CacheService {

    default <T> Optional<T> queryNullableCacheString(String module, String key,
                                                     Callable<T> callable, int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> Optional<T> queryNullableCacheString(String module, String key,
                                                     Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> T queryCacheString(String module, String key,
                                   Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }

    default <T> T queryCacheString(String module, String key,
                                   Callable<T> callable,
                                   int seconds) throws CacheException {
        throw new UnsupportedOperationException();
    }


    default <T> T queryAllCacheString(String module,
                                   Callable<T> callable) throws CacheException {
        throw new UnsupportedOperationException();
    }
}
