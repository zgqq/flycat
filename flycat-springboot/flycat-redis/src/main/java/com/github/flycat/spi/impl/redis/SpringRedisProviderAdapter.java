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
package com.github.flycat.spi.impl.redis;

import com.alibaba.fastjson.JSON;
import com.github.flycat.spi.redis.RedisService;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SpringRedisProviderAdapter implements RedisService, BeanClassLoaderAware{
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
    public void setex(String key, long seconds, String value) {
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

    public void afterPropertiesSet() throws Exception {
        redisTemplate.afterPropertiesSet();
        final RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory instanceof InitializingBean) {
            InitializingBean factory = (InitializingBean) connectionFactory;
            factory.afterPropertiesSet();
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        redisTemplate.setBeanClassLoader(classLoader);
    }

    public void destroy() throws Exception {
        final RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory instanceof DisposableBean) {
            DisposableBean factory = (DisposableBean) connectionFactory;
            factory.destroy();
        }
    }
}
