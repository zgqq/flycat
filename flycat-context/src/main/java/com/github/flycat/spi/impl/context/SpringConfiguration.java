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
package com.github.flycat.spi.impl.context;

import com.github.flycat.spi.context.ApplicationConfiguration;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SpringConfiguration implements ApplicationConfiguration, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfiguration.class);
    private ApplicationContext applicationContext;

    @Override
    public String getString(String key) {
        return getEnvironment().getProperty(key);
    }

    public Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }

    @Override
    public Integer getInteger(String key) {
        return NumberUtils.createInteger(getString(key));
    }

    @Override
    public Boolean getBoolean(String key) {
        final String property = getEnvironment().getProperty(key);
        if (property == null) {
            return null;
        }
        return Boolean.valueOf(property);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
