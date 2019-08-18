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

import com.github.flycat.spi.SpiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MultipleRedisService implements SpiService {
    @Inject
    PrimaryRedisService primaryRedisService;
    @Inject
    SecondaryRedisService secondaryRedisService;

//    @ConditionalOnProperty(name = "redis.primary.enable", havingValue = "true")
//    public static class PrimaryConfig {
//        @Value("${redis.primary.host}")
//        private String host;
//        @Value("${redis.primary.port}")
//        private int port;
//        @Value("${redis.primary.password}")

    @PostConstruct
    public void createRedis() {
        final String primaryHost = getString("flycat.redis.primary.host");
        final Integer primaryPort = getInteger("flycat.redis.primary.port");
        final String primaryPassword = getString("flycat.redis.primary.password");
        final StringRedisTemplate redisTemplate =
                createRedisTemplate(primaryHost, primaryPort, primaryPassword);
        primaryRedisService.setProvider(new SpringRedisProviderAdapter(redisTemplate));

        final String secondaryHost = getString("flycat.redis.secondary.host");
        final Integer secondaryPort = getInteger("flycat.redis.secondary.port");
        final String secondaryPassword = getString("flycat.redis.secondary.password");

        final StringRedisTemplate secondaryRedisTemplate =
                createRedisTemplate(secondaryHost, secondaryPort, secondaryPassword);
        secondaryRedisService.setProvider(new SpringRedisProviderAdapter(secondaryRedisTemplate));
    }

    public StringRedisTemplate createRedisTemplate(String host, Integer port, String password) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        if (StringUtils.isBlank(password)) {
            redisStandaloneConfiguration.setPassword(RedisPassword.none());
        } else {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setHostName(host);
        LettuceConnectionFactory redisConnectionFactory =
                new LettuceConnectionFactory(redisStandaloneConfiguration);

        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }
}
