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
package com.github.flycat.spi.queue;

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
