package com.github.bootbox.web.api;

public interface ApiFactory {

    Object createApiResult(int code, String message);

    default Object createUnknownExceptionResult() {
        return null;
    }
}
