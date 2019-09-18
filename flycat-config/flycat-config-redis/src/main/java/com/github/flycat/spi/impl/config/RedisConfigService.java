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
package com.github.flycat.spi.impl.config;

import com.alibaba.fastjson.JSON;
import com.github.flycat.spi.config.ConfigException;
import com.github.flycat.spi.config.ConfigService;
import com.github.flycat.spi.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
@Named
public class RedisConfigService implements ConfigService {

    private final ConcurrentHashMap<String, RedisConfig>
            systemConfigMap = new ConcurrentHashMap<>();
    private final RedisService redisClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfigService.class);

    @Inject
    public RedisConfigService(RedisService redisService) {
        this.redisClient = redisService;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                    post();
                }, 5, 30, TimeUnit.SECONDS
        );
    }

    public RedisConfig getRedisConfig(String key) {
        RedisConfig systemConfig = systemConfigMap.get(key);
        if (systemConfig == null) {
            return getAndRefreshConfig(key);
        }
        return systemConfig;
    }

    private RedisConfig getAndRefreshConfig(String key) {
        RedisConfig systemConfig;
        String string = redisClient.get(key);
        systemConfig = new RedisConfig(key, JSON.parseObject(string));
        systemConfigMap.put(key, systemConfig);
        return systemConfig;
    }

    public void post() {
        LOGGER.info("Refreshing config");
        for (Map.Entry<String, RedisConfig> configEntry : systemConfigMap.entrySet()) {
            try {
                String key = configEntry.getKey();
                systemConfigMap.put(key, getAndRefreshConfig(key));
            } catch (Throwable throwable) {
                LOGGER.info("Unable to refresh config, {}", configEntry, throwable);
            }
        }
    }

    @Override
    public String getConfig(String dataId) throws ConfigException {
        String string = redisClient.get(dataId);
        return string;
    }

    @Override
    public String getConfig(String dataId, long timeoutMs) throws ConfigException {
        String string = redisClient.get(dataId);
        return string;
    }

    @Override
    public String getConfig(String dataId, String group, long timeoutMs) throws ConfigException {
        String string = redisClient.get(dataId);
        return string;
    }

    @Override
    public <T> T getJsonConfig(String dataId, Class<T> type) throws ConfigException {
        final RedisConfig redisConfig = getRedisConfig(dataId);
        return redisConfig.toType(type);
    }

    @Override
    public <T> T getJsonConfig(String dataId, String name, Class<T> type) throws ConfigException {
        final RedisConfig redisConfig = getRedisConfig(dataId);
        return redisConfig.getConfigValue(dataId, type);
    }
}
