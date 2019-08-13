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
