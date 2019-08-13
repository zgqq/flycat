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
package com.github.bootbox.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SpringRedisProviderAdapter implements RedisService {
    private final StringRedisTemplate redisTemplate;

    public SpringRedisProviderAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
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
    public void hsetAsJson(String key, String hashKey, Object object) {
        redisTemplate.boundHashOps(key).put(hashKey, JSON.toJSONString(object));
    }

    @Override
    public void setex(String key, String value, long seconds) {
        redisTemplate.boundValueOps(key).set(value, seconds, TimeUnit.SECONDS);
    }

    @Override
    public <T> T getJsonObject(String key, Class<T> clazz) {
        return JSON.parseObject(redisTemplate.boundValueOps(key).get(), clazz);
    }

    @Override
    public void setexAsJson(String key, Object object, long seconds) {
        redisTemplate.boundValueOps(key).set(JSON.toJSONString(object), seconds, TimeUnit.SECONDS);
    }
}
