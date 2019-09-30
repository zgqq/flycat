package com.github.flycat.dispatch;

public interface ExecutorJob<T> {
     void run(T executor);
}
