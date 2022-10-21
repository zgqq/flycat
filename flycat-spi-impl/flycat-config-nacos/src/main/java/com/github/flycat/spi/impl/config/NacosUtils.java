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
package com.github.flycat.spi.impl.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.flycat.spi.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class NacosUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosUtils.class);

    public static ConfigService createConfigService(
            String serverAddr, String username, String password) {
        try {
            // Initialize the configuration service,
            // and the console automatically obtains the following parameters
            // through the sample code.
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            properties.put(PropertyKeyConst.USERNAME, username);
            properties.put(PropertyKeyConst.PASSWORD, password);
            LOGGER.info("Creating config service, {}, {}, {}", serverAddr, username, password);
            ConfigService configService = NacosFactory.createConfigService(properties);
            // Actively get the configuration.
            return configService;
        } catch (NacosException e) {
            throw new ConfigException("Unable to load nacos", e);
        }
    }
}
