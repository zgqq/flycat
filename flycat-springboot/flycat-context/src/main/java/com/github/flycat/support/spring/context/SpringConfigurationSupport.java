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
import org.springframework.core.env.Environment;

import jakarta.inject.Inject;

public class SpringConfigurationSupport implements ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfigurationSupport.class);
    private final Environment environment;

    @Inject
    public SpringConfigurationSupport(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getStringValue(String key) {
        return getEnvironment().getProperty(key);
    }

    @Override
    public String getApplicationName() {
        return getString("spring.application.name");
    }

    public Environment getEnvironment() {
        return environment;
    }
}
