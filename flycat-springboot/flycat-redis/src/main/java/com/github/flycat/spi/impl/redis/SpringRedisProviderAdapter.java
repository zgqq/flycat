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

import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.spi.redis.RedisService;
import com.github.flycat.spi.redis.SessionCallback;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpringRedisProviderAdapter extends SpringRedisOperations implements RedisService, BeanClassLoaderAware {
    private final StringRedisTemplate redisTemplate;

    public SpringRedisProviderAdapter(StringRedisTemplate redisTemplate) {
        super((redisTemplate));
        this.redisTemplate = redisTemplate;
    }

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
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

    @Override
    public <T> T getJsonObject(String key, Class<T> clazz) {
        return JsonUtils.parseObject(redisTemplate.boundValueOps(key).get(), clazz);
    }

    @Override
    public void setexAsJson(String key, Object object, long seconds) {
        redisTemplate.boundValueOps(key).set(JsonUtils.toJsonString(object), seconds, TimeUnit.SECONDS);
    }

    @Override
    public void hsetAsJson(String key, String hashKey, Object object) {
        redisTemplate.boundHashOps(key).put(hashKey, JsonUtils.toJsonString(object));
    }

    @Override
    public <T> T execute(SessionCallback<T> sessionCallback) {
        return redisTemplate.execute(new org.springframework.data.redis.core.SessionCallback<T>() {
            @Override
            public <K, V> T execute(RedisOperations<K, V> operations) throws DataAccessException {
                final SpringRedisOperations redisOperations = new SpringRedisOperations(operations);
                return sessionCallback.execute(redisOperations);
            }
        });
    }

    @Override
    public <T> List<Object> executePipelined(SessionCallback<T> sessionCallback) {
        return redisTemplate.executePipelined(new org.springframework.data.redis.core.SessionCallback<T>() {
            @Override
            public <K, V> T execute(RedisOperations<K, V> operations) throws DataAccessException {
                final SpringRedisOperations redisOperations = new SpringRedisOperations(operations);
                sessionCallback.execute(redisOperations);
                return null;
            }
        });
    }
}
