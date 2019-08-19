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
package com.github.flycat.starter.app.redis;

import com.github.flycat.context.ApplicationContext;
import com.github.flycat.spi.json.JsonService;
import com.github.flycat.spi.redis.RedisService;
import com.github.flycat.spi.task.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {

    @Bean(destroyMethod = "close", initMethod = "start")
    public RefreshRedisConfTask refreshRedisConfTask(
            ApplicationContext applicationContainer,
            RedisService redisClient,
            JsonService jsonService,
            TaskService taskService
    ) {
        RefreshRedisConfTask refreshRedisConfTask = new RefreshRedisConfTask(applicationContainer,
                redisClient, jsonService, taskService);
        return refreshRedisConfTask;
    }
}
