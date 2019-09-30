package com.github.flycat.dispatch;

import com.google.common.collect.Lists;

import java.util.List;

public class AbstractDispatcher<T> {
    private List<T> eventExecutorList = Lists.newArrayList();

    public void setEventExecutorList(List<T> eventExecutorList) {
        this.eventExecutorList = eventExecutorList;
    }

    public void addExecutor(T eventExecutor) {
        eventExecutorList.add(eventExecutor);
    }

    public List<T> getExecutors() {
        return (List<T>) eventExecutorList;
    }

    protected final void execute(ExecutorJob<T> t){
        final List<T> executors = getExecutors();
        for (T executor : executors) {
            t.run(executor);
        }
    }
}
