package com.github.flycat.spi.impl.redis;

import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.spi.redis.RedisOperations;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SpringRedisOperations implements RedisOperations {

    private final org.springframework.data.redis.core.RedisOperations<String, String> redisTemplate;

    public SpringRedisOperations(org.springframework.data.redis.core.RedisOperations redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public String hget(String redisKey, String key) {
        return (String) redisTemplate.boundHashOps(redisKey).get(key);
    }

    @Override
    public Set<String> smembers(String key) {
        return redisTemplate.boundSetOps(key).members();
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        Map entries = redisTemplate.boundHashOps(key).entries();
        return entries;
    }


    @Override
    public void setex(String key, long seconds, String value) {
        redisTemplate.boundValueOps(key).set(value, seconds, TimeUnit.SECONDS);
    }


    @Override
    public Long del(String... keys) {
        return redisTemplate.delete(Lists.newArrayList(keys));
    }

    @Override
    public boolean zadd(String key, double score, String member) {
        return redisTemplate.boundZSetOps(key).add(member, score);
    }

    @Override
    public Set<String> zrange(String key, int start, int end) {
        return redisTemplate.boundZSetOps(key).range(start, end);
    }

    @Override
    public boolean setnx(String key, String value) {
        return redisTemplate.boundValueOps(key).setIfAbsent(value);
    }

    @Override
    public boolean expire(String key, int seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void multi() {
        redisTemplate.multi();
    }

    @Override
    public List<Object> exec() {
        return redisTemplate.exec();
    }

    @Override
    public long incr(String key) {
        return redisTemplate.boundValueOps(key).increment();
    }
}