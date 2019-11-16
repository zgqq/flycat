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
package com.github.flycat.service;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.*;

@Singleton
@Named
public class TrackedExecutorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorService.class);
    private ThreadFactory threadFactory;
    private ThreadPoolExecutor ioExecutor;
    private ThreadPoolExecutor cpuExecutor;

    {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (ioExecutor != null) {
                    try {
                        LOGGER.info("Closing io executor");
                        ioExecutor.shutdown();
                        ioExecutor.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                    }
                }

                if (cpuExecutor != null) {
                    try {
                        LOGGER.info("Closing cpu executor");
                        cpuExecutor.shutdown();
                        cpuExecutor.awaitTermination(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
    }

    public synchronized <T> Future<?> submitCpuTask(final Runnable task) {
        tryCreateThreadFactory();
        if (cpuExecutor == null) {
            cpuExecutor = new ThreadPoolExecutor(4, 10,
                    60, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(1024), threadFactory,
                    new ThreadPoolExecutor.AbortPolicy());
        }
        return submitTask(cpuExecutor, task);
    }

    public synchronized <T> Future<T> submitIOTask(final Runnable task) {
        tryCreateThreadFactory();
        if (ioExecutor == null) {
            ioExecutor = new ThreadPoolExecutor(2, 20,
                    60, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(1024), threadFactory,
                    new ThreadPoolExecutor.AbortPolicy());
        }
        return submitTask(ioExecutor, task);
    }

    private <T> Future<T> submitTask(ThreadPoolExecutor executor, Runnable task) {
        return (Future<T>) executor.submit(wrap(task, clientTrace(), Thread.currentThread().getName()));
    }


    private Runnable wrap(final Runnable task, final Exception clientStack, String clientThreadName) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            MDC.setContextMap(contextMap);
            try {
                task.run();
            } catch (Exception e) {
                LOGGER.error("Exception {} in task submitted from thread {} here:", e, clientThreadName, clientStack);
                throw e;
            } finally {
                MDC.setContextMap(Maps.newHashMap());
            }
        };
    }


    private <T> Callable<T> wrap(final Callable<T> task, final Exception clientStack, String clientThreadName) {
        return () -> {
            try {
                return task.call();
            } catch (Exception e) {
                LOGGER.error("Exception {} in task submitted from thread {} here:", e, clientThreadName, clientStack);
                throw e;
            }
        };
    }

    private Exception clientTrace() {
        return new Exception("Client stack trace");
    }


    public synchronized void tryCreateThreadFactory() {
        if (threadFactory == null) {
            threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("traced-executor-%d")
                    .setDaemon(true)
                    .build();
        }
    }
}
