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
package com.github.flycat.spi.redis;

import java.util.Map;
import java.util.Set;

public class AbstractRedisService implements RedisService {
    private RedisService provider;

    public AbstractRedisService() {
    }

    public AbstractRedisService(RedisService provider) {
        this.provider = provider;
    }

    @Override
    public String hget(String redisKey, String key) {
        return provider.hget(redisKey, key);
    }

    @Override
    public Set<String> smembers(String key) {
        return provider.smembers(key);
    }

    @Override
    public String get(String key) {
        return provider.get(key);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return provider.hgetAll(key);
    }

    @Override
    public void hsetAsJson(String key, String hashKey, Object object) {
        provider.hsetAsJson(key, hashKey, object);
    }

    @Override
    public void setex(String key, long seconds, String value) {
        provider.setex(key, seconds, value);
    }

    @Override
    public <T> T getJsonObject(String key, Class<T> clazz) {
        return provider.getJsonObject(key, clazz);
    }

    @Override
    public void setexAsJson(String key, Object object, long seconds) {
        provider.setexAsJson(key, object, seconds);
    }

    @Override
    public Long del(String... keys) {
        return provider.del(keys);
    }

    public void setProvider(RedisService provider) {
        this.provider = provider;
    }
}
