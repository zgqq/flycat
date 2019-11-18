package com.github.flycat.spi.cache;

public class ExecuteResult<T> {
    private final boolean executed;
    private final T returnValue;

    public ExecuteResult(boolean executed, T returnValue) {
        this.executed = executed;
        this.returnValue = returnValue;
    }

    public boolean isExecuted() {
        return executed;
    }

    public T getReturnValue() {
        return returnValue;
    }
}
