package com.github.bootbox.config.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Properties;

public class NacosUtils {

    public static ConfigService createConfigService(
            String serverAddr) {
        try {
            // Initialize the configuration service,
            // and the console automatically obtains the following parameters
            // through the sample code.
            Properties properties = new Properties();
            properties.put("serverAddr", serverAddr);
            ConfigService configService = NacosFactory.createConfigService(properties);
            // Actively get the configuration.
            return configService;
        } catch (NacosException e) {
            throw new RuntimeException("Unable to load nacos", e);
        }
    }
}
