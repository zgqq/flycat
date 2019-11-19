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

import com.github.flycat.context.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class SpringConfiguration extends SpringConfigurationSupport implements ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfiguration.class);
    private final ApplicationContext applicationContext;

    @Inject
    public SpringConfiguration(ApplicationContext applicationContext) {
        super(applicationContext.getEnvironment());
        this.applicationContext = applicationContext;
    }

    @Override
    public String getStringValue(String key) {
        return getEnvironment().getProperty(key);
    }

    @Override
    public String getApplicationName() {
        return getString("spring.application.name");
    }
}
