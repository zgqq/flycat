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
package com.github.flycat.context;

import com.github.flycat.util.io.FileUtils;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public final class ContextUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextUtils.class);

    private static final AtomicReference<ApplicationContext> CONTAINER_HOLDER = new AtomicReference<>();

    private ContextUtils() {
    }

    public static synchronized void setContextHolder(ApplicationContext containerHolder) {
        if (containerHolder == null) {
            throw new UnsupportedOperationException("Container holder is null");
        }
        if (ContextUtils.CONTAINER_HOLDER.get() != null) {
            LOGGER.info("Updating new container");
//            throw new UnsupportedOperationException("Already set container");
        }
        ContextUtils.CONTAINER_HOLDER.set(containerHolder);
        LOGGER.info("Set container holder, container:{}", containerHolder);
    }

    public static synchronized ApplicationContext getContextHolder() {
        return ContextUtils.CONTAINER_HOLDER.get();
    }

    public static <T> T getBean(Class<T> type) {
        final ApplicationContext contextHolder = getContextHolder();
        if (contextHolder != null) {
            return contextHolder.getBean(type);
        }
        return null;
    }

    public static ApplicationConfiguration getApplicationConfiguration() {
        final ApplicationContext contextHolder = getContextHolder();
        if (contextHolder == null) {
            return null;
        }
        if (!contextHolder.isActive()) {
            return null;
        }
        final ApplicationConfiguration bean = contextHolder.getBean(ApplicationConfiguration.class);
        return bean;
    }

    static final List<String> PRODUCT_ENV_WORDS = Lists.newArrayList("production", "product", "prod", "ali", "docker");

    public static boolean isTestProfile() {
        final String property = getCurrentProfile();
        return PRODUCT_ENV_WORDS.stream().noneMatch(word -> word.equals(property));
    }

    private static String getCurrentProfile() {
        return System.getProperty("spring.profiles.active");
    }

    static final List<String> STRING_ARRAY_LIST = Lists.newArrayList(PRODUCT_ENV_WORDS);

    static {
        STRING_ARRAY_LIST.add("test");
    }

    public static boolean isTestServerProfile() {
        final String currentProfile = getCurrentProfile();
        return "test".equals(currentProfile);
    }

    public static boolean isLocalProfile() {
        final String currentProfile = getCurrentProfile();
        return STRING_ARRAY_LIST.stream().noneMatch(word -> word.equals(currentProfile));
    }

    public static String getApplicationName() {
        ContextFreeConfiguration contextFreeConfiguration = createContextFreeConfiguration();
        return contextFreeConfiguration.getApplicationName();
    }

    public static ContextFreeConfiguration createContextFreeConfiguration() {
        return new ContextFreeConfiguration(getApplicationConfiguration());
    }

    static final List<String> CONTAINER_ENV_WORDS = Lists.newArrayList("docker");

    static Function<List<String>, Boolean> inEnv = strings -> {
        String currentProfile = getCurrentProfile();
        return strings.stream().anyMatch(word -> word.equals(currentProfile));
    };

    public static boolean serverRunning() {
        if (inEnv.apply(CONTAINER_ENV_WORDS)) {
            return !FileUtils.fileExists("/tmp/app_stop");
        }
        return true;
    }

    public static boolean isProd() {
        return inEnv.apply(PRODUCT_ENV_WORDS);
    }
}
