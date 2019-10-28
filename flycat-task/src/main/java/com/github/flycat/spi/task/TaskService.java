package com.github.flycat.spi.task;

public interface TaskService {
   void addFixedDelayTaskInSecond(Runnable runnable, long intervalSeconds, long initialDelaySeconds);

   void addFixedDelayTaskInSecond(Runnable runnable, long intervalSeconds);

   void addFixedDelayTask(Runnable runnable, long interval, long initialDelay);

}
