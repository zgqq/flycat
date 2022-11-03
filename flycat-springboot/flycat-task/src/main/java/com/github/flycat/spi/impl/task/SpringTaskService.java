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
package com.github.flycat.spi.impl.task;

import com.github.flycat.spi.task.TaskService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Named
@Singleton
public class SpringTaskService implements TaskService, DisposableBean {
    private final ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
    private final ScheduledThreadPoolExecutor localExecutor = new ScheduledThreadPoolExecutor(1);
    private final Set<ScheduledTask> scheduledTasks = Collections.synchronizedSet(new LinkedHashSet<>(16));

    {
        registrar.setScheduler(localExecutor);
    }

    @Override
    public void addFixedDelayTaskInSecond(Runnable runnable, long intervalSeconds, long initialDelaySeconds) {
        final FixedDelayTask task = new FixedDelayTask(runnable, intervalSeconds * 1000, initialDelaySeconds * 1000);
        addFixedDelayTask(task);
    }

    @Override
    public void addFixedDelayTaskInSecond(Runnable runnable, long intervalSeconds) {
        final FixedDelayTask task = new FixedDelayTask(runnable, intervalSeconds * 1000, 0);
        addFixedDelayTask(task);
    }

    @Override
    public void addFixedDelayTask(Runnable runnable, long interval, long initialDelay) {
        final FixedDelayTask task = new FixedDelayTask(runnable, interval, initialDelay);
        addFixedDelayTask(task);
    }

    private void addFixedDelayTask(FixedDelayTask task) {
        registrar.addFixedDelayTask(task);
        final ScheduledTask scheduledTask = registrar.scheduleFixedDelayTask(task);
        scheduledTasks.add(scheduledTask);
    }

    @Override
    public void destroy() throws Exception {
        for (ScheduledTask task : this.scheduledTasks) {
            task.cancel();
        }
        localExecutor.shutdownNow();
    }
}
