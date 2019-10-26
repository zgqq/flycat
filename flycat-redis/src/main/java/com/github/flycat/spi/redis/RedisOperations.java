package com.github.flycat.spi.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisOperations {

    String hget(String redisKey, String key);

    Set<String> smembers(String key);

    String get(String key);

    Map<String, String> hgetAll(String key);


    void setex(String key, long seconds, String value);


    Long del(String... key);

    boolean zadd(String key, double score, String member);

    Set<String> zrange(String key, int start, int end);

    boolean setnx(String key, String value);

    boolean expire(String key, int seconds);

    void multi();

    List<Object> exec();

}
