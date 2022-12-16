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
import com.github.flycat.util.DateTimeUtils;
import com.github.flycat.util.ExceptionUtils;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SpringBootPlatform {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootPlatform.class);
    private static Class<?> primarySource;
// get a RuntimeMXBean reference
    public static void run(Class<?> primarySource, String[] args, Class<? extends Module>... modules) {
        try {
//            String property = System.getProperty("spring.profiles.active");
//            if (StringUtils.isEmpty(property)) {
//                System.setProperty("spring.profiles.active", "nativelocal");
//            }
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = runtimeMxBean.getInputArguments();
            Map<String, String> env = System.getenv();
            Stopwatch started = Stopwatch.createStarted();
            SpringBootPlatform.primarySource = primarySource;
            ContextManager.beforeRun(new RunContext(modules));
            System.out.println("Preparing spring application run "+ DateTimeUtils.toISO8601DateTimeFormat(new Date()));
            ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(primarySource, args);
            LOGGER.info("Run env, primarySource:{}, args:{}, jvmArgs:{}, env:{}", primarySource.getName(), args, arguments, env);
//            CompletableFuture<Void> attachAgent = CompletableFuture.runAsync(() -> {
//                try {
//
//                    AttachAgent.attachAgent();
//                } catch (Throwable e) {
//                    LOGGER.warn("Unable to attach agent", e);
////                    throw new RuntimeException("Unable to attach agent", e);
//                }
//            });
//            attachAgent.get();
            ApplicationContext applicationContext = configurableApplicationContext.getBean(ApplicationContext.class);
            ContextManager.afterRun(applicationContext);
            started.stop();
            LOGGER.info("Started spring boot application, cost {}", started);
        } catch (Exception e) {

            // spring boot 3.0 need
            if (e instanceof SpringApplication.AbandonedRunException) {
                throw (SpringApplication.AbandonedRunException)e;
            }

            boolean equals = e.getClass().getName().equals("org.springframework.boot.devtools." +
                    "restart.SilentExitExceptionHandler$SilentExitException");
            if (!equals) {
                System.err.println("Server startup failed: " + ExceptionUtils.getStackTrace(e));
                LOGGER.warn("Startup exception", e);
                if (!ContextUtils.isLocalProfile()) {
                    LOGGER.info("Unable to startup server, system exit");
                    System.exit(0);
                }
            } else {
                // https://github.com/spring-projects/spring-boot/issues/3100
                LOGGER.info("Dev tool restart exception, ignored ");
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
