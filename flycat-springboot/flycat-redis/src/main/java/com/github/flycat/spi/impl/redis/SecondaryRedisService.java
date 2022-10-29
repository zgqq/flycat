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

import com.github.flycat.spi.redis.AbstractRedisService;
import com.github.flycat.spi.redis.RedisService;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named
public class SecondaryRedisService extends AbstractRedisService  {
    public SecondaryRedisService() {
    }

    public SecondaryRedisService(RedisService provider) {
        super(provider);
    }
}
