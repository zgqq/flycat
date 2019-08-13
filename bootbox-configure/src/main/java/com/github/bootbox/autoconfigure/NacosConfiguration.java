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
