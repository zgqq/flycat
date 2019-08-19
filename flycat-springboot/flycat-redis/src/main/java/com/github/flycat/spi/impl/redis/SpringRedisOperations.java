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
package com.github.flycat.spi.impl.redis;

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
    public String hGet(String redisKey, String key) {
        return (String) redisTemplate.boundHashOps(redisKey).get(key);
    }

    @Override
    public Set<String> sMembers(String key) {
        return redisTemplate.boundSetOps(key).members();
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Map<String, String> hGetAll(String key) {
        Map entries = redisTemplate.boundHashOps(key).entries();
        return entries;
    }


    @Override
    public void setEx(String key, long seconds, String value) {
        redisTemplate.boundValueOps(key).set(value, seconds, TimeUnit.SECONDS);
    }


    @Override
    public Long del(String... keys) {
        return redisTemplate.delete(Lists.newArrayList(keys));
    }

    @Override
    public Boolean zAdd(String key, double score, String member) {
        return redisTemplate.boundZSetOps(key).add(member, score);
    }

    @Override
    public Set<String> zRange(String key, int start, int end) {
        return redisTemplate.boundZSetOps(key).range(start, end);
    }

    @Override
    public Boolean setNx(String key, String value) {
        return redisTemplate.boundValueOps(key).setIfAbsent(value);
    }

    @Override
    public Boolean expire(String key, int seconds) {
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
    public Long incr(String key) {
        return redisTemplate.boundValueOps(key).increment();
    }
}
