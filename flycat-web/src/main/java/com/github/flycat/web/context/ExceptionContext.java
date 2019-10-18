package com.github.flycat.web.context;

public class ExceptionContext {
    private final Throwable throwable;
    private final boolean responseBodyHandler;

    public ExceptionContext(Throwable throwable, boolean responseBodyHandler) {
        this.throwable = throwable;
        this.responseBodyHandler = responseBodyHandler;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isResponseBodyHandler() {
        return responseBodyHandler;
    }
}
