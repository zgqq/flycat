package com.github.flycat.spi.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisOperations {

    String hGet(String redisKey, String key);

    Set<String> sMembers(String key);

    String get(String key);

    Map<String, String> hGetAll(String key);

    void setEx(String key, long seconds, String value);

    Long del(String... key);

    Boolean zAdd(String key, double score, String member);

    Set<String> zRange(String key, int start, int end);

    Boolean setNx(String key, String value);

    Boolean expire(String key, int seconds);

    void multi();

    List<Object> exec();

    Long incr(String key);
}
