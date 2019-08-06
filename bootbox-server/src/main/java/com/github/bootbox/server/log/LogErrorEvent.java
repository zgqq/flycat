package com.github.bootbox.server.log;

import ch.qos.logback.classic.Logger;

public class LogErrorEvent {
    private final Logger logger;
    private final String message;
    private final Throwable throwable;

    public LogErrorEvent(Logger logger, String message, Throwable throwable) {
        this.logger = logger;
        this.message = message;
        this.throwable = throwable;
    }

    public Logger getLogger() {
        return logger;
    }


    public String getMessage() {
        return message;
    }


    public Throwable getThrowable() {
        return throwable;
    }
}
