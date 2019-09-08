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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class SpringConfiguration implements ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfiguration.class);
    private final ApplicationContext applicationContext;

    public SpringConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String getStringValue(String key) {
        return getEnvironment().getProperty(key);
    }

    public Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }

}
