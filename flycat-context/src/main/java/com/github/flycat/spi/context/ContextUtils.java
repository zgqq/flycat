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
package com.github.flycat.spi.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

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
            throw new UnsupportedOperationException("Already set container");
        }
        ContextUtils.CONTAINER_HOLDER.set(containerHolder);
        LOGGER.info("Set container holder, container:{}", containerHolder);
    }

    public static synchronized ApplicationContext getContextHolder() {
        return ContextUtils.CONTAINER_HOLDER.get();
    }
}
