package com.github.bootbox.autoconfigure;

import com.github.bootbox.cache.CacheService;
import com.github.bootbox.cache.guava.GuavaCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({GuavaCacheService.class})
public class GuavaCacheConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaCacheConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    public CacheService createCacheProvider() {
        LOGGER.info("Creating cache provider");
        return new GuavaCacheService();
    }
}
