package com.github.flycat.spi.redis;


public interface SessionCallback<T> {
    T execute(RedisOperations redisOperations);
}
