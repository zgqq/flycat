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
package com.github.flycat.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);

    static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("flycat-event-pool").build();

    private static final EventBus EVENT_BUS =
            new AsyncEventBus(
                    new ThreadPoolExecutor(4, 10,
                            60, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(1024), threadFactory,
                            new ThreadPoolExecutor.AbortPolicy())
                    , (exception, context)
                    -> LOGGER.error("Unable to handle event, event:{}, subscriber:{}", context.getEvent(),
                    context.getSubscriberMethod().getName(),
                    exception));


    public static void register(Object obj) {
        EVENT_BUS.register(obj);
    }

    public static void unregister(Object obj) {
        EVENT_BUS.unregister(obj);
    }

    public static void post(Object obj) {
        EVENT_BUS.post(obj);
    }
}
