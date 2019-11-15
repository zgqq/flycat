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

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.spi.SpiService;
import com.github.flycat.spi.redis.RedisService;
import com.github.flycat.util.StringUtils;

public abstract class MultipleRedisService implements SpiService {
    private final PrimaryRedisService primaryRedisService;
    private final SecondaryRedisService secondaryRedisService;
    private final ApplicationConfiguration applicationConfiguration;

    public MultipleRedisService(PrimaryRedisService primaryRedisService,
                                SecondaryRedisService secondaryRedisService,
                                ApplicationConfiguration applicationConfiguration) {
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
        primaryRedisService.setProvider(newRedisService(primaryHost, primaryPort, primaryPassword));

        final String secondaryHost = getString("flycat.redis.secondary.host");
        final Integer secondaryPort = getInteger("flycat.redis.secondary.port");
        final String secondaryPassword = getString("flycat.redis.secondary.password");
        if (StringUtils.isNotBlank(secondaryHost)) {
            secondaryRedisService.setProvider(newRedisService(secondaryHost, secondaryPort, secondaryPassword));
        }
    }

    public abstract RedisService newRedisService(String host, Integer port, String password);

}
