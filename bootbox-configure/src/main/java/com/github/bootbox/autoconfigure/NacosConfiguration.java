package com.github.bootbox.autoconfigure;

import com.github.bootbox.config.ConfigService;
import com.github.bootbox.config.nacos.NacosConfigService;
import com.github.bootbox.config.nacos.NacosUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NacosConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosConfiguration.class);

    @Bean
    @ConditionalOnProperty(value = "nacos.config.server-addr")
    @ConditionalOnClass(ConfigService.class)
    public ConfigService configService(@Value("${nacos.config.server-addr}")
                                               String serverAdd) {
        LOGGER.info("Creating config service, addr:{}", serverAdd);
        final com.alibaba.nacos.api.config.ConfigService configService = NacosUtils.createConfigService(serverAdd);
        return new NacosConfigService(configService);
    }
}
