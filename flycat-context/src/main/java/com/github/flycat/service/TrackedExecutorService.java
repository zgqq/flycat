package com.github.flycat.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
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
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                LOGGER.error("Exception {} in task submitted from thread {} here:", e, clientThreadName, clientStack);
                throw e;
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
                    .setNameFormat("executor-pool-%d")
                    .setDaemon(true)
                    .build();
        }
    }
}
