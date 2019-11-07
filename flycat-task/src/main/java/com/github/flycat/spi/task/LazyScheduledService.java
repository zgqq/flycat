package com.github.flycat.spi.task;

import java.util.concurrent.Callable;

public class LazyScheduledService<V> {

    private volatile boolean scheduled = false;
    private Object lock = new Object();
    private final Callable<V> callable;

    public LazyScheduledService(Callable<V> callable) {
        this.callable = callable;
    }

    public V trySchedule() {
        if (!scheduled) {
            synchronized (lock) {
                if (!scheduled) {
                    try {
                        V call = callable.call();
                        return call;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        scheduled = true;
                    }
                }
            }
        }
        return null;
    }
}
