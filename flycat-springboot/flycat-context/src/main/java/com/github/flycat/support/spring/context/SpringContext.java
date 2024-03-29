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
package com.github.flycat.support.spring.context;

import com.github.flycat.context.ApplicationContext;
import com.github.flycat.context.ContextUtils;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Iterator;
import java.util.List;

//@Component
@Named
@Singleton
public class SpringContext implements ApplicationContext, InitializingBean  {
    private final org.springframework.context.ApplicationContext applicationContext;

    @Inject
    public SpringContext(org.springframework.context.ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean isActive() {
        return ((AbstractApplicationContext) applicationContext).isActive();
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    @Override
    public Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    @Override
    public <T> Iterator<T> getBeansIterator(Class<T> clazz) {
        ObjectProvider<T> beanProvider = applicationContext.getBeanProvider(clazz);
        return beanProvider.iterator();
    }

    @Override
    public String getApplicationName() {
        return this.applicationContext.getEnvironment().getProperty("spring.application.name");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ContextUtils.setContextHolder(this);
    }
}
