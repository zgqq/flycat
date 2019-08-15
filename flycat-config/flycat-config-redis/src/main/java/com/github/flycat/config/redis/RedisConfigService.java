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
package com.github.flycat.config.redis;

import com.alibaba.fastjson.JSON;
import com.github.flycat.redis.RedisService;
import com.github.flycat.config.ConfigException;
import com.github.flycat.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisConfigService implements ConfigService {

    private final ConcurrentHashMap<String, RedisConfig>
            systemConfigMap = new ConcurrentHashMap<>();
    private final RedisService redisClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfigService.class);

    public RedisConfigService(RedisService redisShardingClient) {
        this.redisClient = redisShardingClient;
    }

    public RedisConfig getRedisConfig(String key) {
        RedisConfig systemConfig = systemConfigMap.get(key);
        if (systemConfig == null) {
            return getAndRefreshBizConfig(key);
        }
        return systemConfig;
    }

    private RedisConfig getAndRefreshBizConfig(String key) {
        RedisConfig systemConfig;
        String string = redisClient.get(key);
        systemConfig = new RedisConfig(key, JSON.parseObject(string));
        systemConfigMap.put(key, systemConfig);
        return systemConfig;
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 30 * 1000)
    public void post() {
        LOGGER.info("Refreshing config");
        for (Map.Entry<String, RedisConfig> configEntry : systemConfigMap.entrySet()) {
            try {
                String key = configEntry.getKey();
                systemConfigMap.put(key, getAndRefreshBizConfig(key));
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
