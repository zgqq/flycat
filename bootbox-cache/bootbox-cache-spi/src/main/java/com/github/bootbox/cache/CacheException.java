package com.github.bootbox.cache;

public class CacheException extends RuntimeException {

    public CacheException() {
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
