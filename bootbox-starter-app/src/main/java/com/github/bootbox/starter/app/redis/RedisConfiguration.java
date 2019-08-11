package com.github.bootbox.starter.app.redis;

import com.github.bootbox.redis.RedisService;
import com.github.bootbox.container.ApplicationContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {

    @Bean(destroyMethod = "close", initMethod = "start")
    public RefreshRedisConfTask refreshRedisConfTask(
            ApplicationContainer applicationContainer,
            RedisService redisClient) {
        RefreshRedisConfTask refreshRedisConfTask = new RefreshRedisConfTask(applicationContainer, redisClient);
        return refreshRedisConfTask;
    }
}
