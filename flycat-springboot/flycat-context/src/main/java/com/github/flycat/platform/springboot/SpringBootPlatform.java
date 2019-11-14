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
package com.github.flycat.platform.springboot;

import com.github.flycat.agent.monitor.AttachAgent;
import com.github.flycat.context.ApplicationContext;
import com.github.flycat.context.ContextManager;
import com.github.flycat.context.ContextUtils;
import com.github.flycat.context.RunContext;
import com.github.flycat.module.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBootPlatform {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootPlatform.class);
    private static Class<?> primarySource;

    public static void run(Class<?> primarySource, String[] args, Class<? extends Module>... modules) {
        try {
            SpringBootPlatform.primarySource = primarySource;
            ContextManager.beforeRun(new RunContext(modules));
            ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(primarySource, args);
            AttachAgent.attachAgent();
            ApplicationContext applicationContext = configurableApplicationContext.getBean(ApplicationContext.class);
            ContextManager.afterRun(applicationContext);
        } catch (Exception e) {
            LOGGER.warn("Startup exception", e);
            if (!ContextUtils.isLocalProfile()) {
                LOGGER.info("Unable to startup server, system exit");
                System.exit(0);
            }
        }
    }

    public static Class<?> getPrimarySource() {
        return primarySource;
    }

    public static void setPrimarySource(Class<?> primarySource) {
        SpringBootPlatform.primarySource = primarySource;
    }
}
