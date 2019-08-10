package com.github.bootbox.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zgq on 17-5-18.
 */
public abstract class AlwaysRestartHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlwaysRestartHandler.class);
    protected Thread worker;
    protected AtomicBoolean stop = new AtomicBoolean(false);

    //new thread
    public void start() {
        Runnable self = this;
        worker = new Thread(self);
        worker.start();
        Thread watcher = new Thread(() -> {
            while (!stop.get()) {
                try {
                    if (!worker.isAlive()) {
                        worker = new Thread(self);
                        worker.start();
                    }
                    TimeUnit.SECONDS.sleep(1L);
                } catch (Throwable e) {
                    LOGGER.error("watch exception!", e);
                }
            }
        });
        watcher.start();
    }

    public final void stop() {
        stop.set(true);
    }
}
