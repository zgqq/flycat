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
package com.github.flycat.autoconfigure;

import com.github.flycat.spi.context.ApplicationContext;
import com.github.flycat.spi.context.ContextUtils;
import com.github.flycat.spi.context.spring.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(ApplicationContext.class)
public class SpringContextConfiguration {

    @Bean
    public ApplicationContext containerHolder(@Autowired org.springframework.context.ApplicationContext webApplicationContext) {
        SpringContext springContainer = new SpringContext(webApplicationContext);
        ContextUtils.setContextHolder(springContainer);
        return springContainer;
    }
}
