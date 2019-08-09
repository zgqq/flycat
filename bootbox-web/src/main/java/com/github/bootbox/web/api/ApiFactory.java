package com.github.bootbox.web.api;

public interface ApiFactory {

    Object createApiResult(int code, String message);

    default Object createUnknownExceptionResult() {
        return null;
    }

    default Object createInvalidTokenResult() {
        return null;
    }

    default Object createAccessDeniedResult(Exception exception) {
        return null;
    }
}
