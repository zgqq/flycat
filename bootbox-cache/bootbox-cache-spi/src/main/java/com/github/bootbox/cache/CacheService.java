package com.github.bootbox.cache;

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
