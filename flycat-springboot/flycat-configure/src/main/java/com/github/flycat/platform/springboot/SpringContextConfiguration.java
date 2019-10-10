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
package com.github.flycat.platform.springboot;

import com.github.flycat.context.bean.annotation.Primary;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@ComponentScan(basePackages = {"com.github.flycat.spi.impl",
        "com.github.flycat.support.spring",
        "com.github.flycat.component",
        "com.github.flycat.service"})
public class SpringContextConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringContextConfiguration.class);

    @Bean
    public static DispatcherRegisterProcessor dispatcherRegisterProcessor() {
        return new DispatcherRegisterProcessor();
    }

    @Bean
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public static BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor() {

        return new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
                    throws BeansException {
                final String[] beanDefinitionNames = registry.getBeanDefinitionNames();
                for (int i = 0; i < beanDefinitionNames.length; i++) {
                    final String beanDefinitionName = beanDefinitionNames[i];
                    final BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
                    final String beanClassName = beanDefinition.getBeanClassName();
                    if (StringUtils.isBlank(beanClassName)) {
                        continue;
                    }
//                    LOGGER.info("Check bean class name, {}", beanClassName);
                    final Primary annotation;
                    try {
                        annotation = Class.forName(beanClassName).getAnnotation(Primary.class);
                        if (annotation != null) {
                            LOGGER.info("Setting primary bean, class:{}", beanClassName);
                            beanDefinition.setPrimary(true);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            }
        };
    }
}
