package com.github.flycat.spi.impl.task;

import com.github.flycat.spi.task.TaskService;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class SpringTaskService implements TaskService {
    private final ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();

    @Override
    public void addFixedDelayTaskInSecond(Runnable runnable, long secondsInterval, long initialDelaySeconds) {
        final FixedDelayTask task = new FixedDelayTask(runnable, secondsInterval * 1000, initialDelaySeconds * 1000);
        registrar.addFixedDelayTask(task);
    }

    @Override
    public void addFixedDelayTaskInSecond(Runnable runnable, long secondsInterval) {
        final FixedDelayTask task = new FixedDelayTask(runnable, secondsInterval * 1000, 0);
        registrar.addFixedDelayTask(task);
    }

    @Override
    public void addFixedDelayTask(Runnable runnable, long interval, long initialDelay) {
        final FixedDelayTask task = new FixedDelayTask(runnable, interval, initialDelay);
        registrar.addFixedDelayTask(task);
        registrar.scheduleFixedDelayTask(task);
    }
}
