package com.github.bootbox.web;

public class BootboxWebException extends RuntimeException {

    public BootboxWebException() {
    }

    public BootboxWebException(String message) {
        super(message);
    }

    public BootboxWebException(String message, Throwable cause) {
        super(message, cause);
    }

    public BootboxWebException(Throwable cause) {
        super(cause);
    }

    public BootboxWebException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
