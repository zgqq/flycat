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
import com.github.flycat.spi.context.ApplicationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class MultipleRedisService implements SpiService {
    private final PrimaryRedisService primaryRedisService;
    private final SecondaryRedisService secondaryRedisService;
    private final ApplicationConfiguration applicationConfiguration;

    @Inject
    public MultipleRedisService(PrimaryRedisService primaryRedisService,
                                SecondaryRedisService secondaryRedisService, ApplicationConfiguration applicationConfiguration) {
        this.primaryRedisService = primaryRedisService;
        this.secondaryRedisService = secondaryRedisService;
        this.applicationConfiguration = applicationConfiguration;
        createRedis();
    }

    @Override
    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

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
        if (StringUtils.isNotBlank(secondaryHost)) {
            final StringRedisTemplate secondaryRedisTemplate =
                    createRedisTemplate(secondaryHost, secondaryPort, secondaryPassword);
            secondaryRedisService.setProvider(new SpringRedisProviderAdapter(secondaryRedisTemplate));
        }
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
