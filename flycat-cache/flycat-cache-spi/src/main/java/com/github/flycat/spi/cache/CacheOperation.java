package com.github.flycat.spi.cache;

public interface CacheOperation {

    default boolean removeCache(String module, String key) {
        throw new UnsupportedOperationException();
    }

    default boolean removeCache(String module) {
        throw new UnsupportedOperationException();
    }
}
