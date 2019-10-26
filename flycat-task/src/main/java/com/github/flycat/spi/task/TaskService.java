package com.github.flycat.spi.task;

public interface TaskService {
   void addFixedDelayTaskInSecond(Runnable runnable, long secondsInterval, long initialDelaySeconds);

   void addFixedDelayTaskInSecond(Runnable runnable, long secondsInterval);

   void addFixedDelayTask(Runnable runnable, long interval, long initialDelay);

}
