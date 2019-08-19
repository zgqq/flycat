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
package com.github.flycat.support.spring.druid;

import com.github.flycat.component.datasource.druid.DruidStatProperties;
import com.github.flycat.support.spring.BeanDefinitionUtils;
import com.github.flycat.support.spring.context.SpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringDruidLoader implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringDruidLoader.class);

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        final SpringConfiguration springConfiguration = new SpringConfiguration(this.applicationContext);
        final DruidStatProperties druidStatProperties = springConfiguration.load("flycat.datasource.druid",
                DruidStatProperties.class);
        LOGGER.info("Loaded druid config, value:{}", druidStatProperties);
        if (druidStatProperties.getWebStatFilter().isEnabled()) {
            BeanDefinitionUtils.register(registry, FilterRegistrationBean.class,
                    "webStatFilterRegistrationBean", () ->
                            SpringDruidUtils.webStatFilterRegistrationBean(druidStatProperties));
        }
        if (druidStatProperties.getStatViewServlet().isEnabled()) {
            BeanDefinitionUtils.register(registry, ServletRegistrationBean.class,
                    "statViewServletRegistrationBean",
                    () ->
                            SpringDruidUtils.statViewServletRegistrationBean(druidStatProperties));
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
